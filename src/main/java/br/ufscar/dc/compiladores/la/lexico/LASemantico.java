package br.ufscar.dc.compiladores.la.lexico;

import br.ufscar.dc.compiladores.la.lexico.LAParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;

public class LASemantico extends LAParserBaseVisitor<TipoLA> {

    Escopos escopos = new Escopos();
    public List<String> errosSemanticos = new ArrayList<>();

    private void adicionarErro(int linha, String mensagem) {
        errosSemanticos.add("Linha " + linha + ": " + mensagem);
    }

    // Método auxiliar para resolver o tipo de identificadores simples ou compostos (p.nome)
    private TipoLA verificarIdentificador(LAParser.IdentificadorContext ctx) {
        String nomeCompleto = ctx.getText();
        String[] partes = nomeCompleto.split("\\.");
        String nomePrincipal = partes[0];

        if (!escopos.existeNaHierarquia(nomePrincipal)) {
            adicionarErro(ctx.start.getLine(), "identificador " + nomePrincipal + " nao declarado");
            return TipoLA.INDEFINIDO;
        }

        Simbolo s = escopos.obterNaHierarquia(nomePrincipal);
    
        // Se for acesso a campo (ex: p.nome)
        if (partes.length > 1) {
            String nomeCampo = partes[1];
            // Busca o campo na lista de campos do símbolo 's'
            Simbolo campoEncontrado = null;
            for (Simbolo c : s.getCampos()) {
                if (c.getNome().equals(nomeCampo)) {
                    campoEncontrado = c;
                    break;
                }
            }

            if (campoEncontrado == null) {
                adicionarErro(ctx.start.getLine(), "identificador " + nomeCompleto + " nao declarado");
                return TipoLA.INDEFINIDO;
            }
            return campoEncontrado.getTipo();
        }

        return s.getTipo();
    } 

    @Override
    public TipoLA visitPrograma(LAParser.ProgramaContext ctx) {
        escopos.entrarEscopo();
        super.visitPrograma(ctx);
        escopos.sairEscopo();
        return null;
    }

   @Override
    public TipoLA visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // 1. CASO DE DECLARAÇÃO DE VARIÁVEIS (Ex: declare x : ^inteiro)
        if (ctx.variavel() != null) {
            // Captura a string tal como escrita no código (ex: "^inteiro")
            String tipoCompleto = ctx.variavel().tipo().getText(); 
            // Remove o ^ apenas para validar se o tipo base existe no sistema
            String tipoBaseLimpo = tipoCompleto.replace("^", "");
            
            TipoLA tipoBase = converterTipo(tipoBaseLimpo);

            // Se o tipoBase for INDEFINIDO, pode ser um tipo customizado (ex: t_registro)
            if (tipoBase == TipoLA.INDEFINIDO && !escopos.existeNaHierarquia(tipoBaseLimpo)) {
                adicionarErro(ctx.variavel().tipo().start.getLine(), "tipo " + tipoBaseLimpo + " nao declarado");
            }

            for (LAParser.IdentificadorContext identCtx : ctx.variavel().identificador()) {
                String nomeVar = identCtx.getText();
                if (escopos.existeNoEscopoAtual(nomeVar)) {
                    adicionarErro(identCtx.start.getLine(), "identificador " + nomeVar + " ja declarado anteriormente");
                } else {
                    // SALVAMOS O TIPO COMPLETO: Fundamental para a Pessoa 2 validar atribuições de ponteiro futuramente
                    Simbolo s = new Simbolo(nomeVar, Simbolo.Categoria.VARIAVEL, tipoBase, tipoCompleto);
                    
                    // Se a variável for de um tipo customizado que é Registro, copiamos os campos para a variável
                    if (tipoBase == TipoLA.INDEFINIDO && escopos.existeNaHierarquia(tipoBaseLimpo)) {
                        Simbolo sTipo = escopos.obterNaHierarquia(tipoBaseLimpo);
                        if (sTipo.getTipo() == TipoLA.REGISTRO) {
                            for (Simbolo campo : sTipo.getCampos()) {
                                s.adicionarCampo(campo);
                            }
                        }
                    }
                    escopos.adicionarNoEscopoAtual(nomeVar, s);
                }
            }
        } 
        
        // 2. CASO DE DEFINIÇÃO DE NOVO TIPO (Ex: tipo t_ptr : ^inteiro)
        else if (ctx.getText().startsWith("tipo")) {
            String nomeTipo = ctx.IDENT().getText();
            
            if (escopos.existeNoEscopoAtual(nomeTipo)) {
                adicionarErro(ctx.IDENT().getSymbol().getLine(), "identificador " + nomeTipo + " ja declarado anteriormente");
            } else {
                String textoTipoDefinido = ctx.tipo().getText();
                TipoLA tipoBaseDef;
                
                // Verifica se a definição é um registro ou um apelido para tipo básico/ponteiro
                if (ctx.tipo().registro() != null) {
                    tipoBaseDef = TipoLA.REGISTRO;
                } else {
                    tipoBaseDef = converterTipo(textoTipoDefinido.replace("^", ""));
                }

                // Criamos o símbolo do TIPO com o texto original (ex: ^inteiro ou registro...)
                Simbolo sTipo = new Simbolo(nomeTipo, Simbolo.Categoria.TIPO, tipoBaseDef, textoTipoDefinido);
                
                // Se for de fato um registro, popula os campos internos
                if (ctx.tipo().registro() != null) {
                    for (LAParser.VariavelContext vCtx : ctx.tipo().registro().variavel()) {
                        String tipoCampoCompleto = vCtx.tipo().getText();
                        TipoLA tCampoBase = converterTipo(tipoCampoCompleto.replace("^", ""));
                        
                        for (LAParser.IdentificadorContext idCtx : vCtx.identificador()) {
                            sTipo.adicionarCampo(new Simbolo(idCtx.getText(), Simbolo.Categoria.VARIAVEL, tCampoBase, tipoCampoCompleto));
                        }
                    }
                }
                escopos.adicionarNoEscopoAtual(nomeTipo, sTipo);
            }
        }

        return null;
    }

    @Override
    public TipoLA visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext identCtx : ctx.identificador()) {
            verificarIdentificador(identCtx);
        }
        return null;
    }


    @Override
    public TipoLA visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;
        
        if (ctx.identificador() != null) {
            TipoLA tipo = verificarIdentificador(ctx.identificador());
            // Se houver '^' antes do identificador em uma expressão, 
            // o tipo resultante é o tipo base apontado.
            return tipo; 
        }
        
        // Verifique se o CIRCUNFLEXO está presente sem identificador (erro de sintaxe)
        if (ctx.CIRCUNFLEXO() != null && ctx.identificador() == null) {
            return TipoLA.INDEFINIDO;
        }

        if (ctx.expressao() != null && !ctx.expressao().isEmpty()) return visitExpressao(ctx.expressao(0));
        return TipoLA.INDEFINIDO;
    }

    // --- MÉTODOS DE EXPRESSÃO (MANTIDOS IGUAIS) ---

    @Override
    public TipoLA visitExpressao(LAParser.ExpressaoContext ctx) {
        TipoLA ret = visitTermo_logico(ctx.termo_logico(0));
        for (int i = 1; i < ctx.termo_logico().size(); i++) {
            visitTermo_logico(ctx.termo_logico(i));
            ret = TipoLA.LOGICO;
        }
        return ret;
    }

    @Override
    public TipoLA visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        TipoLA ret = visitFator_logico(ctx.fator_logico(0));
        for (int i = 1; i < ctx.fator_logico().size(); i++) {
            visitFator_logico(ctx.fator_logico(i));
            ret = TipoLA.LOGICO;
        }
        return ret;
    }

    @Override
    public TipoLA visitFator_logico(LAParser.Fator_logicoContext ctx) {
        return visitParcela_logica(ctx.parcela_logica());
    }

    @Override
    public TipoLA visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) return visitExp_relacional(ctx.exp_relacional());
        return TipoLA.LOGICO;
    }

    @Override
    public TipoLA visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        TipoLA t1 = visitExp_aritmetica(ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            visitExp_aritmetica(ctx.exp_aritmetica(1));
            return TipoLA.LOGICO;
        }
        return t1;
    }

    @Override
    public TipoLA visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = visitTermo(ctx.termo(0));
        for (int i = 1; i < ctx.termo().size(); i++) {
            TipoLA m = visitTermo(ctx.termo(i));
            ret = Escopos.tipoResultanteExpressao(ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitTermo(LAParser.TermoContext ctx) {
        TipoLA ret = visitFator(ctx.fator(0));
        for (int i = 1; i < ctx.fator().size(); i++) {
            TipoLA m = visitFator(ctx.fator(i));
            ret = Escopos.tipoResultanteExpressao(ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitFator(LAParser.FatorContext ctx) {
        TipoLA ret = visitParcela(ctx.parcela(0));
        for (int i = 1; i < ctx.parcela().size(); i++) {
            TipoLA m = visitParcela(ctx.parcela(i));
            ret = Escopos.tipoResultanteExpressao(ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitParcela(LAParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) return visitParcela_unario(ctx.parcela_unario());
        else return visitParcela_nao_unario(ctx.parcela_nao_unario());
    }

    @Override
    public TipoLA visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        // 1. Obtemos o texto completo do identificador (ex: "x" ou "pessoa.nome")
        String nomeVar = ctx.identificador().getText();
        
        // Pegamos apenas a base do nome para verificar no escopo se não for registro
        // Se for um identificador simples, pesquisamos direto
        if (!escopos.existeNaHierarquia(nomeVar)) {
            adicionarErro(ctx.identificador().start.getLine(), "identificador " + nomeVar + " nao declarado");
        } else {
            // 2. Obtemos o símbolo da variável para saber o tipo dela
            Simbolo s = escopos.obterNaHierarquia(nomeVar);
            TipoLA tipoVariavel = s.getTipo();

            // 3. Verificamos se a atribuição é para um ponteiro (se houver '^' antes do identificador)
            // No seu parser: cmdAtribuicao : CIRCUNFLEXO? identificador ATRIBUICAO expressao ;
            boolean isPonteiroAtribuicao = ctx.CIRCUNFLEXO() != null;

            // 4. Inferimos o tipo da expressão do lado direito
            TipoLA tipoExpressao = verificarTipo(ctx.expressao());

            // 5. Validação de compatibilidade
            // Se a atribuição tem '^', estamos atribuindo ao VALOR apontado, 
            // então comparamos o tipo base do ponteiro com a expressão.
            if (isPonteiroAtribuicao) {
                if (!tiposCompativeisPonteiro(tipoVariavel, tipoExpressao)) {
                    adicionarErro(ctx.identificador().start.getLine(), 
                        "atribuicao nao compativel para ^" + nomeVar);
                }
            } else {
                // Atribuição normal
                if (!tiposCompativeis(tipoVariavel, tipoExpressao)) {
                    adicionarErro(ctx.identificador().start.getLine(), 
                        "atribuicao nao compativel para " + nomeVar);
                }
            }
        }
        return null;
    }

    @Override
    public TipoLA visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;
        return TipoLA.INDEFINIDO;
    }

    private TipoLA converterTipo(String tipoStr) {
        if (tipoStr.equals("inteiro")) return TipoLA.INTEIRO;
        if (tipoStr.equals("real")) return TipoLA.REAL;
        if (tipoStr.equals("literal")) return TipoLA.LITERAL;
        if (tipoStr.equals("logico")) return TipoLA.LOGICO;
        return TipoLA.INDEFINIDO;
    }

        // 1. O motor de inferência de tipos (versão básica para começar)
    private TipoLA verificarTipo(LAParser.ExpressaoContext ctx) {
        // Por enquanto, vamos retornar o tipo da primeira parcela logica
        // Você precisará expandir isso para percorrer toda a árvore da expressão
        return visitExpressao(ctx);
    }

    // 2. Compatibilidade para atribuição normal
    private boolean tiposCompativeis(TipoLA t1, TipoLA t2) {
        if (t1 == t2) return true;
        if (t1 == TipoLA.REAL && t2 == TipoLA.INTEIRO) return true;
        return false;
    }

    // 3. Compatibilidade específica para ponteiros
    private boolean tiposCompativeisPonteiro(TipoLA tVariavel, TipoLA tExpressao) {
        // Se tVariavel é um ponteiro de inteiro, o valor atribuído via ^ deve ser inteiro
        // Aqui você deve verificar se tVariavel é ponteiro e comparar o tipo base dele com tExpressao
        return tVariavel == tExpressao; 
    }
}