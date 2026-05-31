package br.ufscar.dc.compiladores.la.lexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visitador responsavel pela Geracao de Codigo C (Trabalho 5).
 *
 * <p>Percorre a AST gerada pelo ANTLR e traduz cada construcao da
 * Linguagem Algoritmica (LA) para o equivalente em C executavel pelo GCC.</p>
 *
 * <h2>Divisao de responsabilidades:</h2>
 * <ul>
 *   <li><b>Pessoa 1</b> – Esqueleto C, declaracoes basicas, leia/escreva</li>
 *   <li><b>Pessoa 2</b> – Estruturas de controle, operadores, ponteiros</li>
 *   <li><b>Pessoa 3</b> – Registros, sub-rotinas, constantes, documentacao e testes GCC</li>
 * </ul>
 */
public class LAGeradorC extends LAParserBaseVisitor<Void> {

    /** Acumula o codigo C traduzido. */
    public StringBuilder saida = new StringBuilder();

    /**
     * Mapeia identificadores (e campos como {@code reg.idade}) para o tipo LA.
     * Usado para escolher formatadores corretos em printf/scanf.
     */
    private Map<String, String> tabelaTipos = new HashMap<>();

    /**
     * Campos de tipos customizados ({@code tipo Nome: registro ...}).
     * Chave: nome do tipo; valor: mapa campo -> tipo LA.
     */
    private Map<String, Map<String, String>> definicaoTipos = new HashMap<>();

    /** Tipos typedef ja emitidos (evita redeclaracao). */
    private Set<String> tiposDefinidos = new HashSet<>();

    /** Indica se o codigo gerado usa strcpy (requer {@code #include <string.h>}). */
    private boolean precisaStringH = false;

    /** Nivel de indentacao atual (0 = escopo global). */
    private int indent = 0;

    // =========================================================
    // UTILITARIOS INTERNOS
    // =========================================================

    /** Retorna uma string com {@code indent} niveis de 4 espacos. */
    private String ind() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) sb.append("    ");
        return sb.toString();
    }

    /** Registra o tipo LA de um identificador ou campo composto. */
    private void registrarTipo(String nome, String tipoLA) {
        tabelaTipos.put(nome, tipoLA);
    }

    /** Copia os tipos dos campos de um tipo customizado para uma variavel. */
    private void copiarCamposDoTipo(String nomeVar, String nomeTipo) {
        Map<String, String> campos = definicaoTipos.get(nomeTipo);
        if (campos == null) return;
        for (Map.Entry<String, String> e : campos.entrySet()) {
            registrarTipo(nomeVar + "." + e.getKey(), e.getValue());
        }
    }

    /** Resolve o tipo LA de uma expressao percorrendo a arvore sintatica. */
    private String inferirTipoExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx == null) return "inteiro";
        String tipo = "inteiro";
        for (LAParser.Termo_logicoContext tl : ctx.termo_logico()) {
            String t = inferirTipoTermoLogico(tl);
            if ("real".equals(t)) return "real";
            if ("literal".equals(t)) return "literal";
            tipo = t;
        }
        return tipo;
    }

    private String inferirTipoTermoLogico(LAParser.Termo_logicoContext ctx) {
        String tipo = "inteiro";
        for (LAParser.Fator_logicoContext fl : ctx.fator_logico()) {
            String t = inferirTipoFatorLogico(fl);
            if ("real".equals(t)) return "real";
            if ("literal".equals(t)) return "literal";
            tipo = t;
        }
        return tipo;
    }

    private String inferirTipoFatorLogico(LAParser.Fator_logicoContext ctx) {
        LAParser.Parcela_logicaContext pl = ctx.parcela_logica();
        if (pl.PALAVRA_CHAVE_VERDADEIRO() != null || pl.PALAVRA_CHAVE_FALSO() != null) {
            return "logico";
        }
        return inferirTipoExpRelacional(pl.exp_relacional());
    }

    private String inferirTipoExpRelacional(LAParser.Exp_relacionalContext ctx) {
        String t1 = inferirTipoExpAritmetica(ctx.exp_aritmetica(0));
        if (ctx.op_relacional() != null) return "logico";
        return t1;
    }

    private String inferirTipoExpAritmetica(LAParser.Exp_aritmeticaContext ctx) {
        String tipo = inferirTipoTermo(ctx.termo(0));
        for (int i = 1; i < ctx.termo().size(); i++) {
            String t = inferirTipoTermo(ctx.termo(i));
            if ("real".equals(tipo) || "real".equals(t)) return "real";
        }
        return tipo;
    }

    private String inferirTipoTermo(LAParser.TermoContext ctx) {
        String tipo = inferirTipoFator(ctx.fator(0));
        for (int i = 1; i < ctx.fator().size(); i++) {
            String t = inferirTipoFator(ctx.fator(i));
            if ("real".equals(tipo) || "real".equals(t)) return "real";
        }
        return tipo;
    }

    private String inferirTipoFator(LAParser.FatorContext ctx) {
        String tipo = inferirTipoParcela(ctx.parcela(0));
        for (int i = 1; i < ctx.parcela().size(); i++) {
            String t = inferirTipoParcela(ctx.parcela(i));
            if ("real".equals(tipo) || "real".equals(t)) return "real";
        }
        return tipo;
    }

    private String inferirTipoParcela(LAParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) return inferirTipoParcelaUnario(ctx.parcela_unario());
        return inferirTipoParcelaNaoUnario(ctx.parcela_nao_unario());
    }

    private String inferirTipoParcelaUnario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_REAL() != null) return "real";
        if (ctx.NUM_INT() != null) return "inteiro";
        if (ctx.identificador() != null) return obterTipoExpressao(ctx.identificador().getText());
        if (!ctx.expressao().isEmpty()) return inferirTipoExpressao(ctx.expressao(0));
        return "inteiro";
    }

    private String inferirTipoParcelaNaoUnario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return "literal";
        if (ctx.identificador() != null) return obterTipoExpressao(ctx.identificador().getText());
        return "inteiro";
    }

    /**
     * Resolve o tipo LA de um identificador a partir do texto.
     * Tenta caminho completo ({@code reg.idade}) antes do identificador raiz.
     */
    private String obterTipoExpressao(String exprTexto) {
        String limpo = exprTexto.replaceAll("\\[.*", "");
        if (tabelaTipos.containsKey(limpo)) return tabelaTipos.get(limpo);
        String raiz = limpo.split("\\.")[0];
        return tabelaTipos.getOrDefault(raiz, "inteiro");
    }

    /** Verifica se o destino e do tipo literal (atribuicao requer strcpy). */
    private boolean isDestinoLiteral(String lhs) {
        return "literal".equals(tabelaTipos.get(lhs));
    }

    private String converterTipoParaC(String tipoLA) {
        switch (tipoLA) {
            case "inteiro": return "int";
            case "real":    return "float";
            case "literal": return "char";
            case "logico":  return "int";
            default:        return tipoLA;
        }
    }

    /**
     * Gera as declaracoes dos campos internos de um registro.
     *
     * @param reg       contexto do registro na gramatica
     * @param prefixVar prefixo da variavel (ex: {@code reg}) ou {@code null} ao definir um tipo
     * @param nomeTipo  nome do tipo customizado sendo definido, ou {@code null}
     */
    private void gerarCamposRegistro(LAParser.RegistroContext reg, String prefixVar, String nomeTipo) {
        Map<String, String> camposTipo = nomeTipo != null ? new HashMap<>() : null;

        for (LAParser.VariavelContext vCtx : reg.variavel()) {
            String tipoTexto = vCtx.tipo().getText();
            String tipoBase  = tipoTexto.replace("^", "");
            boolean isPonteiro = tipoTexto.startsWith("^");
            String tipoC = converterTipoParaC(tipoBase);

            for (LAParser.IdentificadorContext idCtx : vCtx.identificador()) {
                String nomeCampo = idCtx.IDENT(0).getText();

                if (prefixVar != null) {
                    registrarTipo(prefixVar + "." + nomeCampo, tipoBase);
                }
                if (camposTipo != null) {
                    camposTipo.put(nomeCampo, tipoBase);
                }

                if (tipoBase.equals("literal")) {
                    saida.append(ind()).append("char ").append(nomeCampo).append("[80];\n");
                } else if (isPonteiro) {
                    saida.append(ind()).append(tipoC).append(" *").append(nomeCampo).append(";\n");
                } else {
                    saida.append(ind()).append(tipoC).append(" ").append(nomeCampo).append(";\n");
                }
            }
        }

        if (nomeTipo != null && camposTipo != null) {
            definicaoTipos.put(nomeTipo, camposTipo);
        }
    }

    /** Monta a lista de parametros formais de uma sub-rotina. */
    private String gerarAssinaturaParametros(LAParser.ParametrosContext ctx) {
        if (ctx == null) return "";
        List<String> params = new ArrayList<>();
        for (LAParser.ParametroContext pCtx : ctx.parametro()) {
            String tipoTexto = pCtx.tipo_estendido().getText();
            String tipoBase  = tipoTexto.replace("^", "");
            boolean isPonteiro = tipoTexto.startsWith("^");
            String tipoC = converterTipoParaC(tipoBase);

            for (LAParser.IdentificadorContext idCtx : pCtx.identificador()) {
                String nomeParam = idCtx.IDENT(0).getText();
                registrarTipo(nomeParam, tipoBase);

                if (tipoBase.equals("literal")) {
                    params.add("char *" + nomeParam);
                } else if (isPonteiro) {
                    params.add(tipoC + " *" + nomeParam);
                } else if (!tipoC.isEmpty()) {
                    params.add(tipoC + " " + nomeParam);
                } else {
                    params.add(tipoBase + " " + nomeParam);
                }
            }
        }
        return String.join(", ", params);
    }

    private String traduzirExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx == null) return "";

        StringBuilder expr = new StringBuilder();
        for (int i = 0; i < ctx.termo_logico().size(); i++) {
            if (i > 0) expr.append(" || ");
            expr.append(traduzirTermoLogico(ctx.termo_logico(i)));
        }
        return expr.toString();
    }

    private String traduzirTermoLogico(LAParser.Termo_logicoContext ctx) {
        StringBuilder expr = new StringBuilder();
        for (int i = 0; i < ctx.fator_logico().size(); i++) {
            if (i > 0) expr.append(" && ");
            expr.append(traduzirFatorLogico(ctx.fator_logico(i)));
        }
        return expr.toString();
    }

    private String traduzirFatorLogico(LAParser.Fator_logicoContext ctx) {
        String parcela = traduzirParcelaLogica(ctx.parcela_logica());
        if (ctx.PALAVRA_CHAVE_NAO() != null) return "!" + parcela;
        return parcela;
    }

    private String traduzirParcelaLogica(LAParser.Parcela_logicaContext ctx) {
        if (ctx.PALAVRA_CHAVE_VERDADEIRO() != null) return "1";
        if (ctx.PALAVRA_CHAVE_FALSO() != null)      return "0";
        return traduzirExpRelacional(ctx.exp_relacional());
    }

    private String traduzirExpRelacional(LAParser.Exp_relacionalContext ctx) {
        String ea1 = traduzirExpAritmetica(ctx.exp_aritmetica(0));
        if (ctx.op_relacional() != null && ctx.exp_aritmetica().size() > 1) {
            String op  = traduzirOpRelacional(ctx.op_relacional());
            String ea2 = traduzirExpAritmetica(ctx.exp_aritmetica(1));
            return ea1 + " " + op + " " + ea2;
        }
        return ea1;
    }

    private String traduzirOpRelacional(LAParser.Op_relacionalContext ctx) {
        String texto = ctx.getText();
        switch (texto) {
            case "=":  return "==";
            case "<>": return "!=";
            default:   return texto;
        }
    }

    private String traduzirExpAritmetica(LAParser.Exp_aritmeticaContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(traduzirTermo(ctx.termo(0)));
        for (int i = 1; i < ctx.termo().size(); i++) {
            sb.append(" ").append(ctx.op1(i - 1).getText()).append(" ");
            sb.append(traduzirTermo(ctx.termo(i)));
        }
        return sb.toString();
    }

    private String traduzirTermo(LAParser.TermoContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(traduzirFator(ctx.fator(0)));
        for (int i = 1; i < ctx.fator().size(); i++) {
            sb.append(" ").append(ctx.op2(i - 1).getText()).append(" ");
            sb.append(traduzirFator(ctx.fator(i)));
        }
        return sb.toString();
    }

    private String traduzirFator(LAParser.FatorContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(traduzirParcela(ctx.parcela(0)));
        for (int i = 1; i < ctx.parcela().size(); i++) {
            sb.append(" ").append(ctx.op3(i - 1).getText()).append(" ");
            sb.append(traduzirParcela(ctx.parcela(i)));
        }
        return sb.toString();
    }

    private String traduzirParcela(LAParser.ParcelaContext ctx) {
        String prefixo = (ctx.op_unario() != null) ? "-" : "";
        if (ctx.parcela_unario() != null) {
            return prefixo + traduzirParcelaUnario(ctx.parcela_unario());
        }
        return prefixo + traduzirParcelaNaoUnario(ctx.parcela_nao_unario());
    }

    private String traduzirParcelaUnario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null)  return ctx.NUM_INT().getText();
        if (ctx.NUM_REAL() != null) return ctx.NUM_REAL().getText();

        if (ctx.IDENT() != null && !ctx.expressao().isEmpty()) {
            StringBuilder sb = new StringBuilder(ctx.IDENT().getText()).append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(traduzirExpressao(ctx.expressao(i)));
            }
            sb.append(")");
            return sb.toString();
        }

        if (ctx.identificador() != null) {
            String prefixo = (ctx.CIRCUNFLEXO() != null) ? "*" : "";
            return prefixo + traduzirIdentificador(ctx.identificador());
        }

        if (!ctx.expressao().isEmpty()) {
            return "(" + traduzirExpressao(ctx.expressao(0)) + ")";
        }

        return ctx.getText();
    }

    private String traduzirParcelaNaoUnario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return ctx.CADEIA().getText();
        if (ctx.identificador() != null) {
            return "&" + traduzirIdentificador(ctx.identificador());
        }
        return ctx.getText();
    }

    private String traduzirIdentificador(LAParser.IdentificadorContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.IDENT().size(); i++) {
            if (i > 0) sb.append(".");
            sb.append(ctx.IDENT(i).getText());
        }
        LAParser.DimensaoContext dim = ctx.dimensao();
        if (dim != null) {
            for (LAParser.Exp_aritmeticaContext ea : dim.exp_aritmetica()) {
                sb.append("[").append(traduzirExpAritmetica(ea)).append("]");
            }
        }
        return sb.toString();
    }

    // =========================================================
    // PESSOA 1 — Estrutura principal, declaracoes, leia/escreva
    // =========================================================

    /**
     * Ponto de entrada. Gera includes, declaracoes globais e {@code int main()}.
     */
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n\n");

        indent = 0;
        for (LAParser.Decl_local_globalContext dlg : ctx.declaracoes().decl_local_global()) {
            visit(dlg);
        }

        saida.append("int main() {\n");
        indent = 1;

        for (LAParser.Declaracao_localContext dl : ctx.corpo().declaracao_local()) {
            visit(dl);
        }
        for (LAParser.CmdContext cmd : ctx.corpo().cmd()) {
            visit(cmd);
        }

        saida.append("    return 0;\n");
        saida.append("}\n");

        if (precisaStringH) {
            int pos = saida.indexOf("#include <stdlib.h>\n");
            if (pos >= 0) {
                saida.insert(pos + "#include <stdlib.h>\n".length(), "#include <string.h>\n");
            }
        }

        return null;
    }

    /**
     * Traduz declaracoes locais de variaveis, constantes e tipos customizados.
     */
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.variavel() != null) {
            LAParser.TipoContext tipoCtx = ctx.variavel().tipo();

            if (tipoCtx.registro() != null) {
                for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                    String nomeVar = idCtx.IDENT(0).getText();
                    registrarTipo(nomeVar, "registro");
                    saida.append(ind()).append("struct {\n");
                    indent++;
                    gerarCamposRegistro(tipoCtx.registro(), nomeVar, null);
                    indent--;
                    saida.append(ind()).append("} ").append(nomeVar).append(";\n");
                }
            } else {
                String tipoTexto = tipoCtx.getText();
                String tipoBase  = tipoTexto.replace("^", "");
                boolean isPonteiro = tipoTexto.startsWith("^");
                String tipoC = converterTipoParaC(tipoBase);
                boolean isTipoCustomizado = !tipoBase.equals("inteiro") && !tipoBase.equals("real")
                        && !tipoBase.equals("literal") && !tipoBase.equals("logico");

                for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                    String nomeVar = traduzirIdentificador(idCtx);
                    String nomePrincipal = idCtx.IDENT(0).getText();
                    registrarTipo(nomePrincipal, tipoBase);

                    if (isTipoCustomizado) {
                        copiarCamposDoTipo(nomePrincipal, tipoBase);
                        saida.append(ind()).append(tipoBase).append(" ").append(nomeVar).append(";\n");
                    } else if (tipoBase.equals("literal")) {
                        saida.append(ind()).append("char ").append(nomeVar).append("[80];\n");
                    } else if (isPonteiro) {
                        saida.append(ind()).append(tipoC).append(" *").append(nomeVar).append(";\n");
                    } else if (!tipoC.isEmpty()) {
                        saida.append(ind()).append(tipoC).append(" ").append(nomeVar).append(";\n");
                    }
                }
            }
        } else if (ctx.getChildCount() > 0 && ctx.getChild(0).getText().equals("constante")) {
            String nome = ctx.IDENT().getText();
            String valor = ctx.valor_constante().getText();
            registrarTipo(nome, ctx.tipo_basico().getText());
            saida.append("#define ").append(nome).append(" ").append(valor).append("\n");
        } else if (ctx.getChildCount() > 0 && ctx.getChild(0).getText().equals("tipo")) {
            String nomeTipo = ctx.IDENT().getText();
            LAParser.TipoContext tipoCtx = ctx.tipo();

            if (tipoCtx.registro() != null && !tiposDefinidos.contains(nomeTipo)) {
                tiposDefinidos.add(nomeTipo);
                saida.append("typedef struct {\n");
                indent++;
                gerarCamposRegistro(tipoCtx.registro(), null, nomeTipo);
                indent--;
                saida.append("} ").append(nomeTipo).append(";\n");
            }
        }
        return null;
    }

    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext idCtx : ctx.identificador()) {
            String nomeVar = traduzirIdentificador(idCtx);
            String nomePrincipal = idCtx.IDENT(0).getText();
            String tipo = tabelaTipos.getOrDefault(nomePrincipal, "inteiro");

            if (tipo.equals("literal")) {
                saida.append(ind()).append("gets(").append(nomeVar).append(");\n");
            } else if (tipo.equals("real")) {
                saida.append(ind()).append("scanf(\"%f\", &").append(nomeVar).append(");\n");
            } else {
                saida.append(ind()).append("scanf(\"%d\", &").append(nomeVar).append(");\n");
            }
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext expCtx : ctx.expressao()) {
            String expC = traduzirExpressao(expCtx);
            String expTexto = expCtx.getText();

            if (expTexto.startsWith("\"")) {
                saida.append(ind()).append("printf(").append(expC).append(");\n");
            } else {
                String tipo = inferirTipoExpressao(expCtx);
                if (tipo.equals("real")) {
                    saida.append(ind()).append("printf(\"%f\", ").append(expC).append(");\n");
                } else if (tipo.equals("literal")) {
                    saida.append(ind()).append("printf(\"%s\", ").append(expC).append(");\n");
                } else {
                    saida.append(ind()).append("printf(\"%d\", ").append(expC).append(");\n");
                }
            }
        }
        return null;
    }

    // =========================================================
    // PESSOA 3 — Sub-rotinas (funcao / procedimento)
    // =========================================================

    /**
     * Traduz declaracoes globais de funcao e procedimento.
     * O codigo gerado e emitido antes de {@code int main()}, conforme exigido pelo T5.
     */
    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        boolean isFuncao = ctx.getChild(0).getText().equals("funcao");
        String nome = ctx.IDENT().getText();

        Map<String, String> tiposSalvos = new HashMap<>(tabelaTipos);
        tabelaTipos.clear();
        String params = gerarAssinaturaParametros(ctx.parametros());

        if (isFuncao) {
            String tipoRetTexto = ctx.tipo_estendido().getText().replace("^", "");
            String tipoRetC = converterTipoParaC(tipoRetTexto);
            if (tipoRetC.isEmpty()) tipoRetC = tipoRetTexto;
            saida.append(tipoRetC).append(" ").append(nome).append("(").append(params).append(") {\n");
        } else {
            saida.append("void ").append(nome).append("(").append(params).append(") {\n");
        }

        indent = 1;
        for (LAParser.Declaracao_localContext dl : ctx.declaracao_local()) visit(dl);
        for (LAParser.CmdContext cmd : ctx.cmd()) visit(cmd);

        saida.append("}\n\n");
        indent = 0;
        tabelaTipos = tiposSalvos;
        return null;
    }

    // =========================================================
    // PESSOA 2 — Estruturas de controle, operadores, ponteiros
    // =========================================================

    @Override
    public Void visitCmdSe(LAParser.CmdSeContext ctx) {
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("if (").append(condicao).append(") {\n");

        indent++;
        int posicaoSenao = -1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("senao")) {
                posicaoSenao = i;
                break;
            }
        }

        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            int posCmd = ctx.children.indexOf(cmdCtx);
            if (posicaoSenao == -1 || posCmd < posicaoSenao) visit(cmdCtx);
        }
        indent--;

        if (posicaoSenao != -1) {
            saida.append(ind()).append("} else {\n");
            indent++;
            for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
                int posCmd = ctx.children.indexOf(cmdCtx);
                if (posCmd > posicaoSenao) visit(cmdCtx);
            }
            indent--;
        }

        saida.append(ind()).append("}\n");
        return null;
    }

    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("while (").append(condicao).append(") {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) visit(cmdCtx);
        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    @Override
    public Void visitCmdPara(LAParser.CmdParaContext ctx) {
        String var = ctx.IDENT().getText();
        String inicio = traduzirExpAritmetica(ctx.exp_aritmetica(0));
        String fim = traduzirExpAritmetica(ctx.exp_aritmetica(1));

        saida.append(ind())
             .append("for (").append(var).append(" = ").append(inicio).append("; ")
             .append(var).append(" <= ").append(fim).append("; ")
             .append(var).append("++) {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) visit(cmdCtx);
        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    @Override
    public Void visitCmdFaca(LAParser.CmdFacaContext ctx) {
        saida.append(ind()).append("do {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) visit(cmdCtx);
        indent--;
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("} while (").append(condicao).append(");\n");
        return null;
    }

    @Override
    public Void visitCmdCaso(LAParser.CmdCasoContext ctx) {
        String expr = traduzirExpAritmetica(ctx.exp_aritmetica());
        saida.append(ind()).append("switch (").append(expr).append(") {\n");
        indent++;
        visit(ctx.selecao());

        boolean temSenao = false;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("senao")) {
                temSenao = true;
                break;
            }
        }

        if (temSenao) {
            saida.append(ind()).append("default:\n");
            indent++;
            for (LAParser.CmdContext cmdCtx : ctx.cmd()) visit(cmdCtx);
            indent--;
        }

        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx) {
        for (LAParser.Item_selecaoContext item : ctx.item_selecao()) visit(item);
        return null;
    }

    @Override
    public Void visitItem_selecao(LAParser.Item_selecaoContext ctx) {
        for (LAParser.Numero_intervaloContext ni : ctx.constantes().numero_intervalo()) {
            String texto = ni.getText();
            if (texto.contains("..")) {
                String[] partes = texto.split("\\.\\.");
                int inicio = Integer.parseInt(partes[0].replace("-", "").trim());
                int fim = Integer.parseInt(partes[1].replace("-", "").trim());
                boolean negInicio = partes[0].contains("-");
                boolean negFim = partes[1].contains("-");
                for (int v = (negInicio ? -inicio : inicio); v <= (negFim ? -fim : fim); v++) {
                    saida.append(ind()).append("case ").append(v).append(":\n");
                }
            } else {
                saida.append(ind()).append("case ").append(texto).append(":\n");
            }
        }
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) visit(cmdCtx);
        saida.append(ind()).append("break;\n");
        indent--;
        return null;
    }

    /**
     * Atribuicao: identificador {@code <-} expressao.
     * Campos literal recebem {@code strcpy} em vez de {@code =}.
     */
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String prefixo = (ctx.CIRCUNFLEXO() != null) ? "*" : "";
        String lhs = prefixo + traduzirIdentificador(ctx.identificador());
        String rhs = traduzirExpressao(ctx.expressao());

        if (prefixo.isEmpty() && rhs.startsWith("\"") && isDestinoLiteral(lhs)) {
            precisaStringH = true;
            saida.append(ind()).append("strcpy(").append(lhs).append(", ").append(rhs).append(");\n");
        } else {
            saida.append(ind()).append(lhs).append(" = ").append(rhs).append(";\n");
        }
        return null;
    }

    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        StringBuilder sb = new StringBuilder(ind())
            .append(ctx.IDENT().getText()).append("(");
        for (int i = 0; i < ctx.expressao().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(traduzirExpressao(ctx.expressao(i)));
        }
        sb.append(");\n");
        saida.append(sb);
        return null;
    }

    /** retorne expressao → return expressao; */
    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        saida.append(ind()).append("return ")
             .append(traduzirExpressao(ctx.expressao())).append(";\n");
        return null;
    }
}
