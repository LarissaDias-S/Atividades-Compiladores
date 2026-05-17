package br.ufscar.dc.compiladores.la.lexico;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitador da AST gerada pelo ANTLR que realiza a analise semantica da LA.
 *
 * T3 detectava 4 tipos de erros:
 *   1. Identificador ja declarado no mesmo escopo
 *   2. Tipo nao declarado
 *   3. Identificador nao declarado
 *   4. Atribuicao incompativel com o tipo declarado
 *
 * T4 expande para mais 5 tipos de erros:
 *   1. (Atualizado) Identificador ja declarado - agora inclui funcoes, procedimentos,
 *      ponteiros, registros; mesmo nome nao pode ser reutilizado para categorias
 *      diferentes no mesmo escopo.
 *   2. (Atualizado) Identificador nao declarado - inclui ponteiros, registros, funcoes.
 *   3. Incompatibilidade de argumentos em chamada de procedimento ou funcao
 *      (quantidade, ordem e tipo exatos).
 *   4. (Atualizado) Atribuicao incompativel - agora inclui ponteiros e registros.
 *   5. Uso do comando 'retorne' fora de uma funcao.
 *
 * Todos os erros sao acumulados em 'errosSemanticos' e impressos ao final,
 * pois a analise NAO interrompe ao encontrar um erro.
 */
public class LASemantico extends LAParserBaseVisitor<TipoLA> {

    /** Gerenciador de escopos - pilha de tabelas de simbolos */
    Escopos escopos = new Escopos();

    /** Lista de mensagens de erro acumuladas durante a analise */
    public List<String> errosSemanticos = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Utilitarios internos
    // -------------------------------------------------------------------------

    /**
     * Registra um erro semantico formatado com numero de linha.
     *
     * @param linha    linha do codigo-fonte onde o erro ocorreu
     * @param mensagem descricao do erro
     */
    private void adicionarErro(int linha, String mensagem) {
        errosSemanticos.add("Linha " + linha + ": " + mensagem);
    }

    /**
     * Converte a string do tipo escrita no codigo-fonte para o enum TipoLA.
     * Retorna INDEFINIDO para tipos nao reconhecidos (podem ser tipos customizados).
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
     * Resolve o tipo de um identificador, incluindo acesso a campos de registro
     * (ex: p.nome) e desreferenciacao de ponteiro (ex: ^p).
     * Reporta erro se o identificador ou campo nao estiver declarado.
     */
    private TipoLA verificarIdentificador(LAParser.IdentificadorContext ctx) {
        String nomePrincipal = ctx.IDENT(0).getText();

        if (!escopos.existeNaHierarquia(nomePrincipal)) {
            adicionarErro(ctx.start.getLine(),
                    "identificador " + ctx.getText() + " nao declarado");
            return TipoLA.INDEFINIDO;
        }

        Simbolo s = escopos.obterNaHierarquia(nomePrincipal);

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

    /**
     * T4: valida argumentos vs parametros formais (quantidade e tipo exatos).
     * Agora corretamente referenciada nas visitas de chamadas
     */
    private void verificarChamadaSubrotina(int linha, String nome,
            List<LAParser.ExpressaoContext> argumentos) {
        Simbolo subrotina = escopos.obterNaHierarquia(nome);
        if (subrotina == null) {
            return;
        }

        List<Simbolo> params = subrotina.getParametros();
        if (argumentos.size() != params.size()) {
            adicionarErro(linha,
                    "incompatibilidade de parametros na chamada de " + nome);
            for (LAParser.ExpressaoContext exCtx : argumentos) {
                visitExpressao(exCtx);
            }
            return;
        }

        boolean incompativel = false;
        for (int i = 0; i < argumentos.size(); i++) {
            TipoLA tipoArg = visitExpressao(argumentos.get(i));
            if (!tiposCompativeisParametro(params.get(i), tipoArg, argumentos.get(i))) {
                incompativel = true;
            }
        }
        if (incompativel) {
            adicionarErro(linha,
                    "incompatibilidade de parametros na chamada de " + nome);
        }
    }

    /**
     * Compatibilidade estrita de argumento com parametro formal (tipos exatos).
     */
    private boolean tiposCompativeisParametro(Simbolo param, TipoLA tipoArg,
            LAParser.ExpressaoContext exCtx) {
        if (tipoArg == TipoLA.INDEFINIDO) {
            return false;
        }

        TipoLA tipoParam = param.getTipo();

        if (tipoParam == TipoLA.PONTEIRO && tipoArg == TipoLA.ENDERECO) {
            return true;
        }

        if (tipoParam == TipoLA.REGISTRO && tipoArg == TipoLA.REGISTRO) {
            String nomeArg = obterNomeIdentificadorExpressao(exCtx);
            if (nomeArg != null && escopos.existeNaHierarquia(nomeArg)) {
                Simbolo sArg = escopos.obterNaHierarquia(nomeArg);
                String nomeParam = param.getNomeDoTipo() != null
                        ? param.getNomeDoTipo().replace("^", "") : null;
                String nomeOrigem = sArg.getNomeDoTipo() != null
                        ? sArg.getNomeDoTipo().replace("^", "") : null;
                return nomeParam != null && nomeParam.equals(nomeOrigem);
            }
            return false;
        }

        return tipoParam == tipoArg;
    }

    // =========================================================================
    // PONTO DE ENTRADA
    // =========================================================================

    @Override
    public TipoLA visitPrograma(LAParser.ProgramaContext ctx) {
        escopos.entrarEscopo(false);
        visitChildren(ctx);
        escopos.sairEscopo();
        return null;
    }

    // =========================================================================
    // DECLARACOES LOCAIS (variaveis, constantes, tipos)
    // =========================================================================

    @Override
    public TipoLA visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {

        if (ctx.variavel() != null) {
            String tipoTexto = ctx.variavel().tipo().getText();
            String tipoBase    = tipoTexto.replace("^", "");
            boolean isPonteiro = tipoTexto.startsWith("^");

            TipoLA tipoLA = converterTipo(tipoBase);

            boolean tipoCustomizado = (tipoLA == TipoLA.INDEFINIDO);
            if (tipoCustomizado) {
                if (!escopos.existeNaHierarquia(tipoBase)) {
                    adicionarErro(ctx.variavel().tipo().start.getLine(),
                            "tipo " + tipoBase + " nao declarado");
                } else {
                    Simbolo sTipo = escopos.obterNaHierarquia(tipoBase);
                    tipoLA = sTipo.getTipo(); 
                }
            }

            TipoLA tipoFinal = isPonteiro ? TipoLA.PONTEIRO : tipoLA;

            for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                String nome = idCtx.IDENT(0).getText();

                if (escopos.existeNoEscopoAtual(nome)) {
                    adicionarErro(idCtx.start.getLine(),
                            "identificador " + nome + " ja declarado anteriormente");
                } else {
                    Simbolo s = new Simbolo(nome, Simbolo.Categoria.VARIAVEL, tipoFinal, tipoTexto);
                    if (tipoLA == TipoLA.REGISTRO && escopos.existeNaHierarquia(tipoBase)) {
                        for (Simbolo campo : escopos.obterNaHierarquia(tipoBase).getCampos()) {
                            s.adicionarCampo(campo);
                        }
                    }
                    escopos.adicionarNoEscopoAtual(nome, s);
                }
            }
        }
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
        else if (ctx.getChildCount() > 0 && ctx.getChild(0).getText().equals("tipo")) {
            String nomeTipo = ctx.IDENT().getText();

            if (escopos.existeNoEscopoAtual(nomeTipo)) {
                adicionarErro(ctx.IDENT().getSymbol().getLine(),
                        "identificador " + nomeTipo + " ja declarado anteriormente");
            } else {
                TipoLA tipoBase;
                Simbolo sTipo;

                if (ctx.tipo().registro() != null) {
                    tipoBase = TipoLA.REGISTRO;
                    sTipo = new Simbolo(nomeTipo, Simbolo.Categoria.TIPO, tipoBase, nomeTipo);
                    for (LAParser.VariavelContext vCtx : ctx.tipo().registro().variavel()) {
                        String tipoCampoStr   = vCtx.tipo().getText().replace("^", "");
                        TipoLA tipoCampoLA    = converterTipo(tipoCampoStr);
                        boolean campoPtr      = vCtx.tipo().getText().startsWith("^");
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
                    String textoTipo = ctx.tipo().getText().replace("^", "");
                    tipoBase = converterTipo(textoTipo);
                    sTipo = new Simbolo(nomeTipo, Simbolo.Categoria.TIPO,
                                        tipoBase, ctx.tipo().getText());
                }
                escopos.adicionarNoEscopoAtual(nomeTipo, sTipo);
            }
        }

        return null;
    }

    // =========================================================================
    // DECLARACOES GLOBAIS (funcoes e procedimentos)
    // =========================================================================

    @Override
    public TipoLA visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();
        boolean isFuncao = ctx.getChild(0).getText().equals("funcao");

        if (escopos.existeNoEscopoAtual(nome)) {
            adicionarErro(ctx.IDENT().getSymbol().getLine(),
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            TipoLA tipoRetorno = TipoLA.INDEFINIDO;
            if (isFuncao && ctx.tipo_estendido() != null) {
                String textoTipo = ctx.tipo_estendido().getText().replace("^", "");
                tipoRetorno = converterTipo(textoTipo);
                if (tipoRetorno == TipoLA.INDEFINIDO && escopos.existeNaHierarquia(textoTipo)) {
                    tipoRetorno = escopos.obterNaHierarquia(textoTipo).getTipo();
                }
            }

            Simbolo.Categoria cat = isFuncao
                    ? Simbolo.Categoria.FUNCAO
                    : Simbolo.Categoria.PROCEDIMENTO;

            Simbolo sSubrotina = new Simbolo(nome, cat, tipoRetorno);
            sSubrotina.setTipoRetorno(tipoRetorno);

            if (ctx.parametros() != null) {
                for (LAParser.ParametroContext pCtx : ctx.parametros().parametro()) {
                    String tipoParamStr = pCtx.tipo_estendido().getText().replace("^", "");
                    boolean isPonteiro  = pCtx.tipo_estendido().getText().startsWith("^");
                    TipoLA tipoParam    = converterTipo(tipoParamStr);

                    if (tipoParam == TipoLA.INDEFINIDO
                            && escopos.existeNaHierarquia(tipoParamStr)) {
                        tipoParam = escopos.obterNaHierarquia(tipoParamStr).getTipo();
                    }

                    TipoLA tipoFinal = isPonteiro ? TipoLA.PONTEIRO : tipoParam;

                    for (LAParser.IdentificadorContext idCtx : pCtx.identificador()) {
                        String nomeParam = idCtx.IDENT(0).getText();
                        Simbolo sParam = new Simbolo(nomeParam,
                                Simbolo.Categoria.VARIAVEL,
                                tipoFinal,
                                pCtx.tipo_estendido().getText());
                        sSubrotina.adicionarParametro(sParam);
                    }
                }
            }

            escopos.adicionarNoEscopoAtual(nome, sSubrotina);
        }

        escopos.entrarEscopo(isFuncao);

        if (ctx.parametros() != null) {
            for (LAParser.ParametroContext pCtx : ctx.parametros().parametro()) {
                String tipoParamStr = pCtx.tipo_estendido().getText().replace("^", "");
                boolean isPonteiro  = pCtx.tipo_estendido().getText().startsWith("^");
                TipoLA tipoParam    = converterTipo(tipoParamStr);

                if (tipoParam == TipoLA.INDEFINIDO
                        && escopos.existeNaHierarquia(tipoParamStr)) {
                    tipoParam = escopos.obterNaHierarquia(tipoParamStr).getTipo();
                }

                TipoLA tipoFinal = isPonteiro ? TipoLA.PONTEIRO : tipoParam;

                for (LAParser.IdentificadorContext idCtx : pCtx.identificador()) {
                    String nomeParam = idCtx.IDENT(0).getText();
                    if (escopos.existeNoEscopoAtual(nomeParam)) {
                        adicionarErro(idCtx.start.getLine(),
                                "identificador " + nomeParam + " ja declarado anteriormente");
                    } else {
                        escopos.adicionarNoEscopoAtual(nomeParam,
                                new Simbolo(nomeParam, Simbolo.Categoria.VARIAVEL,
                                            tipoFinal, pCtx.tipo_estendido().getText()));
                    }
                }
            }
        }

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
        for (LAParser.IdentificadorContext idCtx : ctx.identificador()) {
            verificarIdentificador(idCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext exCtx : ctx.expressao()) {
            visitExpressao(exCtx);
        }
        return null;
    }

    @Override
    public TipoLA visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nomeVar = ctx.identificador().IDENT(0).getText();

        if (!escopos.existeNaHierarquia(nomeVar)) {
            adicionarErro(ctx.identificador().start.getLine(),
                    "identificador " + nomeVar + " nao declarado");
            return null;
        }

        TipoLA tipoVariavel = verificarIdentificador(ctx.identificador());

        // CORREÇÃO T4: Se ha '^', acessamos o valor apontado. Extraimos o tipo base.
        if (ctx.CIRCUNFLEXO() != null) {
            Simbolo sPonteiro = escopos.obterNaHierarquia(nomeVar);
            if (sPonteiro != null && sPonteiro.getNomeDoTipo() != null) {
                String tipoBaseStr = sPonteiro.getNomeDoTipo().replace("^", "");
                tipoVariavel = converterTipo(tipoBaseStr);
                
                if (tipoVariavel == TipoLA.INDEFINIDO && escopos.existeNaHierarquia(tipoBaseStr)) {
                    tipoVariavel = escopos.obterNaHierarquia(tipoBaseStr).getTipo();
                }
            }
        }

        TipoLA tipoExpressao = visitExpressao(ctx.expressao());

        if (tipoVariavel != TipoLA.INDEFINIDO && tipoExpressao == TipoLA.INDEFINIDO) {
            adicionarErro(ctx.identificador().start.getLine(),
                    "atribuicao nao compativel para " + ctx.identificador().getText());
            return null;
        }

        if (tipoVariavel == TipoLA.REGISTRO && tipoExpressao == TipoLA.REGISTRO) {
            Simbolo sDestino = escopos.obterNaHierarquia(nomeVar);
            String nomeExpressao = obterNomeIdentificadorExpressao(ctx.expressao());
            if (nomeExpressao != null && escopos.existeNaHierarquia(nomeExpressao)) {
                Simbolo sOrigem = escopos.obterNaHierarquia(nomeExpressao);
                String nomeDestino = sDestino != null ? sDestino.getNomeDoTipo() : null;
                String nomeOrigem  = sOrigem  != null ? sOrigem.getNomeDoTipo()  : null;
                if (nomeDestino == null || !nomeDestino.equals(nomeOrigem)) {
                    adicionarErro(ctx.identificador().start.getLine(),
                            "atribuicao nao compativel para " + ctx.identificador().getText());
                }
            } else {
                adicionarErro(ctx.identificador().start.getLine(),
                        "atribuicao nao compativel para " + ctx.identificador().getText());
            }
        } else if (!Escopos.verificarCompatibilidade(tipoVariavel, tipoExpressao)) {
            adicionarErro(ctx.identificador().start.getLine(),
                    "atribuicao nao compativel para " + ctx.identificador().getText());
        }

        return null;
    }

    private TipoLA combinarTiposExpressao(int linha, TipoLA t1, TipoLA t2) {
        TipoLA resultado = Escopos.tipoResultanteExpressao(t1, t2);
        if (resultado == TipoLA.INDEFINIDO
                && t1 != TipoLA.INDEFINIDO
                && t2 != TipoLA.INDEFINIDO) {
            adicionarErro(linha, "tipo de expressao incompativel");
        }
        return resultado;
    }

    private TipoLA combinarTiposLogicos(int linha, TipoLA t1, TipoLA t2) {
        TipoLA resultado = Escopos.tipoResultanteLogico(t1, t2);
        if (resultado == TipoLA.INDEFINIDO
                && t1 != TipoLA.INDEFINIDO
                && t2 != TipoLA.INDEFINIDO) {
            adicionarErro(linha, "tipo de expressao incompativel");
        }
        return resultado;
    }

    private String obterNomeIdentificadorExpressao(LAParser.ExpressaoContext ctx) {
        try {
            LAParser.Parcela_unarioContext pu = ctx
                    .termo_logico(0)
                    .fator_logico(0)
                    .parcela_logica()
                    .exp_relacional()
                    .exp_aritmetica(0)
                    .termo(0)
                    .fator(0)
                    .parcela(0)
                    .parcela_unario();
            if (pu != null && pu.identificador() != null) {
                return pu.identificador().IDENT(0).getText();
            }
        } catch (Exception e) {
            // Ignorado
        }
        return null;
    }

    @Override
    public TipoLA visitCmdSe(LAParser.CmdSeContext ctx) {
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
    public TipoLA visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
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
        String nomeVar = ctx.IDENT().getText();
        if (!escopos.existeNaHierarquia(nomeVar)) {
            adicionarErro(ctx.start.getLine(),
                    "identificador " + nomeVar + " nao declarado");
        } else {
            Simbolo s = escopos.obterNaHierarquia(nomeVar);
            if (s.getTipo() != TipoLA.INTEIRO) {
                adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
            }
        }
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
        if (!escopos.estaDentroFuncao()) {
            adicionarErro(ctx.start.getLine(),
                    "comando retorne nao permitido nesse escopo");
        }
        visitExpressao(ctx.expressao());
        return null;
    }

    @Override
    public TipoLA visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        String nomeProc = ctx.IDENT().getText();

        if (!escopos.existeNaHierarquia(nomeProc)) {
            adicionarErro(ctx.start.getLine(),
                    "identificador " + nomeProc + " nao declarado");
            return null;
        }

        // T4: Chama a logica da Pessoa 2 para validar parametros!
        verificarChamadaSubrotina(ctx.start.getLine(), nomeProc, ctx.expressao());
        
        return null;
    }

    // =========================================================================
    // EXPRESSOES - inferencia de tipos
    // =========================================================================

    @Override
    public TipoLA visitExpressao(LAParser.ExpressaoContext ctx) {
        TipoLA ret = visitTermo_logico(ctx.termo_logico(0));
        for (int i = 1; i < ctx.termo_logico().size(); i++) {
            TipoLA m = visitTermo_logico(ctx.termo_logico(i));
            ret = combinarTiposLogicos(ctx.start.getLine(), ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        TipoLA ret = visitFator_logico(ctx.fator_logico(0));
        for (int i = 1; i < ctx.fator_logico().size(); i++) {
            TipoLA m = visitFator_logico(ctx.fator_logico(i));
            ret = combinarTiposLogicos(ctx.start.getLine(), ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitFator_logico(LAParser.Fator_logicoContext ctx) {
        TipoLA t = visitParcela_logica(ctx.parcela_logica());
        if (ctx.PALAVRA_CHAVE_NAO() != null) {
            if (t != TipoLA.LOGICO && t != TipoLA.INDEFINIDO) {
                adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
                return TipoLA.INDEFINIDO;
            }
            if (t == TipoLA.INDEFINIDO) {
                return TipoLA.INDEFINIDO;
            }
            return TipoLA.LOGICO;
        }
        return t;
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
            TipoLA t2 = visitExp_aritmetica(ctx.exp_aritmetica(1));
            combinarTiposExpressao(ctx.start.getLine(), t1, t2);
            return TipoLA.LOGICO;
        }
        return t1;
    }

    @Override
    public TipoLA visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = visitTermo(ctx.termo(0));
        for (int i = 1; i < ctx.termo().size(); i++) {
            TipoLA m = visitTermo(ctx.termo(i));
            ret = combinarTiposExpressao(ctx.start.getLine(), ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitTermo(LAParser.TermoContext ctx) {
        TipoLA ret = visitFator(ctx.fator(0));
        for (int i = 1; i < ctx.fator().size(); i++) {
            TipoLA m = visitFator(ctx.fator(i));
            ret = combinarTiposExpressao(ctx.start.getLine(), ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitFator(LAParser.FatorContext ctx) {
        TipoLA ret = visitParcela(ctx.parcela(0));
        for (int i = 1; i < ctx.parcela().size(); i++) {
            TipoLA m = visitParcela(ctx.parcela(i));
            ret = combinarTiposExpressao(ctx.start.getLine(), ret, m);
        }
        return ret;
    }

    @Override
    public TipoLA visitParcela(LAParser.ParcelaContext ctx) {
        TipoLA t;
        if (ctx.parcela_unario() != null) {
            t = visitParcela_unario(ctx.parcela_unario());
        } else {
            t = visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        if (ctx.op_unario() != null) {
            if (t != TipoLA.INTEIRO && t != TipoLA.REAL && t != TipoLA.INDEFINIDO) {
                adicionarErro(ctx.start.getLine(), "tipo de expressao incompativel");
                return TipoLA.INDEFINIDO;
            }
        }
        return t;
    }

    @Override
    public TipoLA visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null)  return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;

        if (ctx.identificador() != null) {
            return verificarIdentificador(ctx.identificador());
        }

        if (ctx.expressao() != null && !ctx.expressao().isEmpty()) {
            return visitExpressao(ctx.expressao(0));
        }

        if (ctx.IDENT() != null) {
            String nomeFuncao = ctx.IDENT().getText();
            if (!escopos.existeNaHierarquia(nomeFuncao)) {
                adicionarErro(ctx.start.getLine(),
                        "identificador " + nomeFuncao + " nao declarado");
                return TipoLA.INDEFINIDO;
            }
            Simbolo sFuncao = escopos.obterNaHierarquia(nomeFuncao);
            
            // T4: Chama a logica da Pessoa 2 para validar parametros de funcao!
            verificarChamadaSubrotina(ctx.start.getLine(), nomeFuncao, ctx.expressao());

            TipoLA ret = sFuncao.getTipoRetorno();
            return (ret != null) ? ret : sFuncao.getTipo();
        }

        return TipoLA.INDEFINIDO;
    }

    @Override
    public TipoLA visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;

        if (ctx.identificador() != null) {
            verificarIdentificador(ctx.identificador());
            return TipoLA.ENDERECO; 
        }

        return TipoLA.INDEFINIDO;
    }
}