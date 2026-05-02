package br.ufscar.dc.compiladores.la.lexico;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitador da AST gerada pelo ANTLR que realiza a análise semântica da LA.
 *
 * Percorre a árvore sintática e detecta os 4 tipos de erros semânticos:
 *   1. Identificador já declarado no mesmo escopo
 *   2. Tipo não declarado
 *   3. Identificador não declarado
 *   4. Atribuição incompatível com o tipo declarado
 *
 * Todos os erros são acumulados em 'errosSemanticos' e impressos ao final,
 * pois a análise NÃO interrompe ao encontrar um erro.
 */
public class LASemantico extends LAParserBaseVisitor<TipoLA> {

    /** Gerenciador de escopos — pilha de tabelas de símbolos */
    Escopos escopos = new Escopos();

    /** Lista de mensagens de erro acumuladas durante a análise */
    public List<String> errosSemanticos = new ArrayList<>();

    /**
     * Registra um erro semântico formatado com número de linha.
     *
     * @param linha   linha do código-fonte onde o erro ocorreu
     * @param mensagem descrição do erro
     */
    private void adicionarErro(int linha, String mensagem) {
        errosSemanticos.add("Linha " + linha + ": " + mensagem);
    }

    /**
     * Converte a string do tipo escrita no código-fonte para o enum TipoLA.
     * Retorna INDEFINIDO para tipos não reconhecidos (podem ser tipos customizados).
     */
    private TipoLA converterTipo(String tipoStr) {
        switch (tipoStr) {
            case "inteiro":  return TipoLA.INTEIRO;
            case "real":     return TipoLA.REAL;
            case "literal":  return TipoLA.LITERAL;
            case "logico":   return TipoLA.LOGICO;
            default:         return TipoLA.INDEFINIDO;
        }
    }

    /**
     * Resolve o tipo de um identificador, incluindo acesso a campos de registro (ex: p.nome).
     * Reporta erro se o identificador ou campo não estiver declarado.
     */
    private TipoLA verificarIdentificador(LAParser.IdentificadorContext ctx) {
        // Pega somente o primeiro IDENT (nome base, antes de qualquer '.')
        String nomePrincipal = ctx.IDENT(0).getText();

        if (!escopos.existeNaHierarquia(nomePrincipal)) {
            adicionarErro(ctx.start.getLine(), "identificador " + nomePrincipal + " nao declarado");
            return TipoLA.INDEFINIDO;
        }

        Simbolo s = escopos.obterNaHierarquia(nomePrincipal);

        // Acesso a campo de registro: ex p.nome → ctx.IDENT() tem tamanho > 1
        if (ctx.IDENT().size() > 1) {
            String nomeCampo = ctx.IDENT(1).getText();
            Simbolo campoEncontrado = null;
            for (Simbolo c : s.getCampos()) {
                if (c.getNome().equals(nomeCampo)) {
                    campoEncontrado = c;
                    break;
                }
            }
            if (campoEncontrado == null) {
                adicionarErro(ctx.start.getLine(),
                        "identificador " + nomePrincipal + "." + nomeCampo + " nao declarado");
                return TipoLA.INDEFINIDO;
            }
            return campoEncontrado.getTipo();
        }

        return s.getTipo();
    }

    // =========================================================================
    // PONTO DE ENTRADA
    // =========================================================================

    @Override
    public TipoLA visitPrograma(LAParser.ProgramaContext ctx) {
        escopos.entrarEscopo();
        visitChildren(ctx);
        escopos.sairEscopo();
        return null;
    }

    // =========================================================================
    // DECLARAÇÕES LOCAIS (variáveis, constantes, tipos)
    // =========================================================================

    @Override
    public TipoLA visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {

        // --- Caso 1: declare <variavel> ---
        if (ctx.variavel() != null) {
            String tipoTexto = ctx.variavel().tipo().getText();
            // Remove '^' para obter o tipo base (ex: "^inteiro" → "inteiro")
            String tipoBase  = tipoTexto.replace("^", "");
            boolean isPonteiro = tipoTexto.startsWith("^");

            TipoLA tipoLA = converterTipo(tipoBase);

            // Verifica se é um tipo customizado (registro declarado com 'tipo')
            boolean tipoCustomizado = (tipoLA == TipoLA.INDEFINIDO);
            if (tipoCustomizado) {
                if (!escopos.existeNaHierarquia(tipoBase)) {
                    adicionarErro(ctx.variavel().tipo().start.getLine(),
                            "tipo " + tipoBase + " nao declarado");
                } else {
                    // Tipo customizado existe — usa REGISTRO como tipo base
                    Simbolo sTipo = escopos.obterNaHierarquia(tipoBase);
                    tipoLA = sTipo.getTipo(); // REGISTRO
                }
            }

            // Tipo final da variável: PONTEIRO se tiver '^', senão o tipo base
            TipoLA tipoFinal = isPonteiro ? TipoLA.PONTEIRO : tipoLA;

            for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                String nome = idCtx.IDENT(0).getText();
                if (escopos.existeNoEscopoAtual(nome)) {
                    adicionarErro(idCtx.start.getLine(),
                            "identificador " + nome + " ja declarado anteriormente");
                } else {
                    Simbolo s = new Simbolo(nome, Simbolo.Categoria.VARIAVEL, tipoFinal, tipoTexto);
                    // Copia campos do tipo registro para a variável
                    if (tipoLA == TipoLA.REGISTRO && escopos.existeNaHierarquia(tipoBase)) {
                        for (Simbolo campo : escopos.obterNaHierarquia(tipoBase).getCampos()) {
                            s.adicionarCampo(campo);
                        }
                    }
                    escopos.adicionarNoEscopoAtual(nome, s);
                }
            }
        }

        // --- Caso 2: constante <IDENT> : <tipo_basico> = <valor> ---
        else if (ctx.getChildCount() > 0 && ctx.getChild(0).getText().equals("constante")) {
            String nomeConst = ctx.IDENT().getText();
            if (escopos.existeNoEscopoAtual(nomeConst)) {
                adicionarErro(ctx.IDENT().getSymbol().getLine(),
                        "identificador " + nomeConst + " ja declarado anteriormente");
            } else {
                TipoLA tipoConst = converterTipo(ctx.tipo_basico().getText());
                escopos.adicionarNoEscopoAtual(nomeConst,
                        new Simbolo(nomeConst, Simbolo.Categoria.CONSTANTE, tipoConst));
            }
        }

        // --- Caso 3: tipo <IDENT> : <tipo> ---
        else if (ctx.getChildCount() > 0 && ctx.getChild(0).getText().equals("tipo")) {
            String nomeTipo = ctx.IDENT().getText();
            if (escopos.existeNoEscopoAtual(nomeTipo)) {
                adicionarErro(ctx.IDENT().getSymbol().getLine(),
                        "identificador " + nomeTipo + " ja declarado anteriormente");
            } else {
                TipoLA tipoBase;
                Simbolo sTipo;

                if (ctx.tipo().registro() != null) {
                    // Tipo registro: coleta os campos internos
                    tipoBase = TipoLA.REGISTRO;
                    sTipo = new Simbolo(nomeTipo, Simbolo.Categoria.TIPO, tipoBase, nomeTipo);
                    for (LAParser.VariavelContext vCtx : ctx.tipo().registro().variavel()) {
                        String tipoCampoStr  = vCtx.tipo().getText().replace("^", "");
                        TipoLA tipoCampoLA   = converterTipo(tipoCampoStr);
                        boolean campoPtr     = vCtx.tipo().getText().startsWith("^");
                        TipoLA tipoCampoFinal = campoPtr ? TipoLA.PONTEIRO : tipoCampoLA;
                        for (LAParser.IdentificadorContext idCtx : vCtx.identificador()) {
                            sTipo.adicionarCampo(new Simbolo(
                                    idCtx.IDENT(0).getText(),
                                    Simbolo.Categoria.VARIAVEL,
                                    tipoCampoFinal,
                                    vCtx.tipo().getText()));
                        }
                    }
                } else {
                    // Tipo alias (ex: tipo t_int : inteiro)
                    String textoTipo = ctx.tipo().getText().replace("^", "");
                    tipoBase = converterTipo(textoTipo);
                    sTipo = new Simbolo(nomeTipo, Simbolo.Categoria.TIPO, tipoBase, ctx.tipo().getText());
                }
                escopos.adicionarNoEscopoAtual(nomeTipo, sTipo);
            }
        }

        return null;
    }

    // =========================================================================
    // DECLARAÇÕES GLOBAIS (funções e procedimentos)
    // =========================================================================

    @Override
    public TipoLA visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();

        // Determina se é função ou procedimento pelo texto inicial
        boolean isFuncao = ctx.getChild(0).getText().equals("funcao");

        if (escopos.existeNoEscopoAtual(nome)) {
            adicionarErro(ctx.IDENT().getSymbol().getLine(),
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            TipoLA tipoRetorno = TipoLA.INDEFINIDO;
            if (isFuncao && ctx.tipo_estendido() != null) {
                String textoTipo = ctx.tipo_estendido().getText().replace("^", "");
                tipoRetorno = converterTipo(textoTipo);
            }
            Simbolo.Categoria cat = isFuncao ? Simbolo.Categoria.FUNCAO : Simbolo.Categoria.PROCEDIMENTO;
            escopos.adicionarNoEscopoAtual(nome, new Simbolo(nome, cat, tipoRetorno));
        }

        // Abre escopo para o corpo da sub-rotina
        escopos.entrarEscopo();

        // Registra os parâmetros no novo escopo
        if (ctx.parametros() != null) {
            for (LAParser.ParametroContext pCtx : ctx.parametros().parametro()) {
                String tipoParamStr = pCtx.tipo_estendido().getText().replace("^", "");
                boolean isPonteiro  = pCtx.tipo_estendido().getText().startsWith("^");
                TipoLA tipoParam    = converterTipo(tipoParamStr);
                TipoLA tipoFinal    = isPonteiro ? TipoLA.PONTEIRO : tipoParam;

                for (LAParser.IdentificadorContext idCtx : pCtx.identificador()) {
                    String nomeParam = idCtx.IDENT(0).getText();
                    if (escopos.existeNoEscopoAtual(nomeParam)) {
                        adicionarErro(idCtx.start.getLine(),
                                "identificador " + nomeParam + " ja declarado anteriormente");
                    } else {
                        escopos.adicionarNoEscopoAtual(nomeParam,
                                new Simbolo(nomeParam, Simbolo.Categoria.VARIAVEL, tipoFinal));
                    }
                }
            }
        }

        // Visita declarações locais e comandos do corpo
        for (LAParser.Declaracao_localContext dlCtx : ctx.declaracao_local()) {
            visitDeclaracao_local(dlCtx);
        }
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }

        escopos.sairEscopo();
        return null;
    }

    // =========================================================================
    // COMANDOS
    // =========================================================================

    @Override
    public TipoLA visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        // Verifica se cada identificador passado ao leia está declarado
        for (LAParser.IdentificadorContext idCtx : ctx.identificador()) {
            verificarIdentificador(idCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        // Valida cada expressão passada ao escreva
        for (LAParser.ExpressaoContext exCtx : ctx.expressao()) {
            visitExpressao(exCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        // Obtém o nome base do identificador (antes de qualquer '.')
        String nomeVar = ctx.identificador().IDENT(0).getText();

        if (!escopos.existeNaHierarquia(nomeVar)) {
            adicionarErro(ctx.identificador().start.getLine(),
                    "identificador " + nomeVar + " nao declarado");
            return null;
        }

        // Resolve o tipo completo do identificador (incluindo acesso a campos)
        TipoLA tipoVariavel = verificarIdentificador(ctx.identificador());

        // Se houver '^' antes do identificador, é atribuição via ponteiro
        if (ctx.CIRCUNFLEXO() != null) {
            tipoVariavel = TipoLA.PONTEIRO;
        }

        // Infere o tipo da expressão do lado direito
        TipoLA tipoExpressao = visitExpressao(ctx.expressao());

        // Verifica compatibilidade
        if (!Escopos.verificarCompatibilidade(tipoVariavel, tipoExpressao)) {
            adicionarErro(ctx.identificador().start.getLine(),
                    "atribuicao nao compativel para " + ctx.identificador().getText());
        }

        return null;
    }

    @Override
    public TipoLA visitCmdSe(LAParser.CmdSeContext ctx) {
        // A condição do 'se' deve ser LOGICO
        TipoLA tipoCond = visitExpressao(ctx.expressao());
        if (tipoCond != TipoLA.LOGICO && tipoCond != TipoLA.INDEFINIDO) {
            adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
        }
        // Visita os comandos dos blocos 'entao' e 'senao'
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        // A condição do 'enquanto' deve ser LOGICO
        TipoLA tipoCond = visitExpressao(ctx.expressao());
        if (tipoCond != TipoLA.LOGICO && tipoCond != TipoLA.INDEFINIDO) {
            adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
        }
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdPara(LAParser.CmdParaContext ctx) {
        // A variável de controle do 'para' deve ser INTEIRO
        String nomeVar = ctx.IDENT().getText();
        if (!escopos.existeNaHierarquia(nomeVar)) {
            adicionarErro(ctx.start.getLine(), "identificador " + nomeVar + " nao declarado");
        } else {
            Simbolo s = escopos.obterNaHierarquia(nomeVar);
            if (s.getTipo() != TipoLA.INTEIRO) {
                adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
            }
        }
        // Visita expressões de limite e corpo
        for (LAParser.Exp_aritmeticaContext eaCtx : ctx.exp_aritmetica()) {
            visitExp_aritmetica(eaCtx);
        }
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        // Visita a expressão de retorno (validação de tipo pode ser expandida)
        visitExpressao(ctx.expressao());
        return null;
    }

    // =========================================================================
    // EXPRESSÕES — inferência de tipos
    // =========================================================================

    @Override
    public TipoLA visitExpressao(LAParser.ExpressaoContext ctx) {
        TipoLA ret = visitTermo_logico(ctx.termo_logico(0));
        for (int i = 1; i < ctx.termo_logico().size(); i++) {
            TipoLA m = visitTermo_logico(ctx.termo_logico(i));
            // 'ou' só é válido entre LOGICO
            ret = (ret == TipoLA.LOGICO && m == TipoLA.LOGICO) ? TipoLA.LOGICO : TipoLA.INDEFINIDO;
        }
        return ret;
    }

    @Override
    public TipoLA visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        TipoLA ret = visitFator_logico(ctx.fator_logico(0));
        for (int i = 1; i < ctx.fator_logico().size(); i++) {
            TipoLA m = visitFator_logico(ctx.fator_logico(i));
            // 'e' só é válido entre LOGICO
            ret = (ret == TipoLA.LOGICO && m == TipoLA.LOGICO) ? TipoLA.LOGICO : TipoLA.INDEFINIDO;
        }
        return ret;
    }

    @Override
    public TipoLA visitFator_logico(LAParser.Fator_logicoContext ctx) {
        TipoLA t = visitParcela_logica(ctx.parcela_logica());
        // 'nao' só é aplicável a LOGICO
        if (ctx.PALAVRA_CHAVE_NAO() != null && t != TipoLA.LOGICO) {
            return TipoLA.INDEFINIDO;
        }
        return t;
    }

    @Override
    public TipoLA visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) return visitExp_relacional(ctx.exp_relacional());
        // 'verdadeiro' ou 'falso'
        return TipoLA.LOGICO;
    }

    @Override
    public TipoLA visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        TipoLA t1 = visitExp_aritmetica(ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            TipoLA t2 = visitExp_aritmetica(ctx.exp_aritmetica(1));
            // Comparação só é válida entre tipos compatíveis
            if (!Escopos.verificarCompatibilidade(t1, t2) && t1 != TipoLA.INDEFINIDO) {
                adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
            }
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
        return visitParcela_nao_unario(ctx.parcela_nao_unario());
    }

    @Override
    public TipoLA visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null)    return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null)   return TipoLA.REAL;
        if (ctx.identificador() != null) return verificarIdentificador(ctx.identificador());
        if (ctx.expressao() != null && !ctx.expressao().isEmpty())
            return visitExpressao(ctx.expressao(0));
        // Chamada de função: IDENT '(' ... ')'
        if (ctx.IDENT() != null) {
            String nomeFuncao = ctx.IDENT().getText();
            if (!escopos.existeNaHierarquia(nomeFuncao)) {
                adicionarErro(ctx.start.getLine(), "identificador " + nomeFuncao + " nao declarado");
                return TipoLA.INDEFINIDO;
            }
            return escopos.obterNaHierarquia(nomeFuncao).getTipo();
        }
        return TipoLA.INDEFINIDO;
    }

    @Override
    public TipoLA visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;
        // '&' identificador — endereço de memória → PONTEIRO
        if (ctx.identificador() != null) {
            verificarIdentificador(ctx.identificador());
            return TipoLA.PONTEIRO;
        }
        return TipoLA.INDEFINIDO;
    }
}
