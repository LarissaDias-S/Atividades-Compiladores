package br.ufscar.dc.compiladores.la.lexico;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitador responsÃ¡vel pela GeraÃ§Ã£o de CÃ³digo C (Trabalho 5).
 *
 * <p>Percorre a AST gerada pelo ANTLR e traduz cada construÃ§Ã£o da
 * Linguagem Algoritmica (LA) para o equivalente em C.</p>
 *
 * <h2>DivisÃ£o de responsabilidades:</h2>
 * <ul>
 *   <li><b>Pessoa 1</b> â€“ Esqueleto C, declaraÃ§Ãµes bÃ¡sicas, leia/escreva</li>
 *   <li><b>Pessoa 2</b> â€“ Estruturas de controle, operadores, ponteiros</li>
 * </ul>
 */
public class LAGeradorC extends LAParserBaseVisitor<Void> {

    /** Acumula o cÃ³digo C traduzido linha a linha. */
    public StringBuilder saida = new StringBuilder();

    /**
     * Guarda o tipo LA de cada variÃ¡vel declarada.
     * Usado para escolher o formatador correto (%d, %f, %s) no printf/scanf.
     */
    private Map<String, String> tabelaTipos = new HashMap<>();

    /** Controla o nÃ­vel de indentaÃ§Ã£o atual (1 = dentro do main). */
    private int indent = 1;

    // =========================================================
    // UTILITÃRIOS INTERNOS
    // =========================================================

    /** Retorna uma string com `indent` nÃ­veis de 4 espaÃ§os. */
    private String ind() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) sb.append("    ");
        return sb.toString();
    }

    /**
     * Traduz uma expressÃ£o LA para texto C.
     * Cuida dos operadores lÃ³gicos/relacionais e de ponteiros.
     * â€” Pessoa 2
     */
    private String traduzirExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx == null) return "";

        StringBuilder expr = new StringBuilder();

        for (int i = 0; i < ctx.termo_logico().size(); i++) {
            if (i > 0) expr.append(" || "); // ou â†’ ||
            expr.append(traduzirTermoLogico(ctx.termo_logico(i)));
        }
        return expr.toString();
    }

    private String traduzirTermoLogico(LAParser.Termo_logicoContext ctx) {
        StringBuilder expr = new StringBuilder();
        for (int i = 0; i < ctx.fator_logico().size(); i++) {
            if (i > 0) expr.append(" && "); // e â†’ &&
            expr.append(traduzirFatorLogico(ctx.fator_logico(i)));
        }
        return expr.toString();
    }

    private String traduzirFatorLogico(LAParser.Fator_logicoContext ctx) {
        String parcela = traduzirParcelaLogica(ctx.parcela_logica());
        if (ctx.PALAVRA_CHAVE_NAO() != null) {
            return "!" + parcela; // nao â†’ !
        }
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

    /**
     * Traduz operadores relacionais LA â†’ C.
     * = â†’ ==   <> â†’ !=   >= â†’ >=   <= â†’ <=   > â†’ >   < â†’ <
     * â€” Pessoa 2
     */
    private String traduzirOpRelacional(LAParser.Op_relacionalContext ctx) {
        String texto = ctx.getText();
        switch (texto) {
            case "=":  return "==";
            case "<>": return "!=";
            case ">=": return ">=";
            case "<=": return "<=";
            case ">":  return ">";
            case "<":  return "<";
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

    /**
     * Traduz parcela unÃ¡ria â€” inclui desreferenciaÃ§Ã£o de ponteiro.
     * ^variavel â†’ *variavel (em C)
     * â€” Pessoa 2
     */
    private String traduzirParcelaUnario(LAParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null)  return ctx.NUM_INT().getText();
        if (ctx.NUM_REAL() != null) return ctx.NUM_REAL().getText();

        if (ctx.IDENT() != null && !ctx.expressao().isEmpty()) {
            // Chamada de funÃ§Ã£o: nome(args)
            StringBuilder sb = new StringBuilder(ctx.IDENT().getText()).append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(traduzirExpressao(ctx.expressao(i)));
            }
            sb.append(")");
            return sb.toString();
        }

        if (ctx.identificador() != null) {
            // ^var â†’ *var  (desreferenciaÃ§Ã£o de ponteiro)
            // â€” Pessoa 2
            String prefixo = (ctx.CIRCUNFLEXO() != null) ? "*" : "";
            return prefixo + traduzirIdentificador(ctx.identificador());
        }

        if (!ctx.expressao().isEmpty()) {
            return "(" + traduzirExpressao(ctx.expressao(0)) + ")";
        }

        return ctx.getText();
    }

    /**
     * Traduz parcela nÃ£o-unÃ¡ria â€” inclui operador de endereÃ§o.
     * &variavel â†’ &variavel (mantido em C)
     * â€” Pessoa 2
     */
    private String traduzirParcelaNaoUnario(LAParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return ctx.CADEIA().getText();
        if (ctx.identificador() != null) {
            // & jÃ¡ Ã© o operador de endereÃ§o em C, mantÃ©m como estÃ¡
            return "&" + traduzirIdentificador(ctx.identificador());
        }
        return ctx.getText();
    }

    /** Traduz identificador (incluindo acesso a campos: registro.campo). */
    private String traduzirIdentificador(LAParser.IdentificadorContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.IDENT().size(); i++) {
            if (i > 0) sb.append(".");
            sb.append(ctx.IDENT(i).getText());
        }
        // DimensÃµes de vetor [exp]
        LAParser.DimensaoContext dim = ctx.dimensao();
        if (dim != null) {
            for (LAParser.Exp_aritmeticaContext ea : dim.exp_aritmetica()) {
                sb.append("[").append(traduzirExpAritmetica(ea)).append("]");
            }
        }
        return sb.toString();
    }

    // =========================================================
    // PESSOA 1 â€” Estrutura principal, declaraÃ§Ãµes, leia/escreva
    // =========================================================

    /**
     * Ponto de entrada. Gera o esqueleto C com includes e main().
     */
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n\n");

        // Visita declaraÃ§Ãµes globais (subrotinas ficam antes do main)
        for (LAParser.Decl_local_globalContext dlg : ctx.declaracoes().decl_local_global()) {
            visit(dlg);
        }

        saida.append("int main() {\n");

        // Corpo: declaraÃ§Ãµes locais e comandos
        for (LAParser.Declaracao_localContext dl : ctx.corpo().declaracao_local()) {
            visit(dl);
        }
        for (LAParser.CmdContext cmd : ctx.corpo().cmd()) {
            visit(cmd);
        }

        saida.append("    return 0;\n");
        saida.append("}\n");
        return null;
    }

    /**
     * Traduz declaraÃ§Ãµes locais de variÃ¡veis.
     * inteiro â†’ int | real â†’ float | literal â†’ char[80]
     */
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.variavel() != null) {
            String tipoTexto = ctx.variavel().tipo().getText();
            String tipoBase  = tipoTexto.replace("^", "");
            boolean isPonteiro = tipoTexto.startsWith("^");

            String tipoC = converterTipoParaC(tipoBase);

            for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                String nomeVar = traduzirIdentificador(idCtx);
                String nomePrincipal = idCtx.IDENT(0).getText();
                tabelaTipos.put(nomePrincipal, tipoBase);

                if (tipoBase.equals("literal")) {
                    saida.append(ind()).append("char ").append(nomeVar).append("[80];\n");
                } else if (isPonteiro) {
                    saida.append(ind()).append(tipoC).append(" *").append(nomeVar).append(";\n");
                } else if (!tipoC.isEmpty()) {
                    saida.append(ind()).append(tipoC).append(" ").append(nomeVar).append(";\n");
                }
            }
        }
        return null;
    }

    private String converterTipoParaC(String tipoLA) {
        switch (tipoLA) {
            case "inteiro": return "int";
            case "real":    return "float";
            case "literal": return "char";
            case "logico":  return "int";
            default:        return tipoLA; // tipos customizados (registros/typedef)
        }
    }

    /**
     * Gera scanf / gets para o comando leia.
     */
    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (int i = 0; i < ctx.identificador().size(); i++) {
            LAParser.IdentificadorContext idCtx = ctx.identificador(i);
            String nomeVar = traduzirIdentificador(idCtx);
            String nomePrincipal = idCtx.IDENT(0).getText();
            String tipo = tabelaTipos.getOrDefault(nomePrincipal, "inteiro");

            // Se o leia vier com ^ (ponteiro), ctx tem CIRCUNFLEXO tokens por Ã­ndice
            boolean temCircunflexo = false;
            // Verificamos no texto original do contexto antes do identificador
            if (ctx.CIRCUNFLEXO() != null && !ctx.CIRCUNFLEXO().isEmpty()) {
                // hÃ¡ pelo menos um ^; assume ponteiro
                temCircunflexo = true;
            }

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

    /**
     * Gera printf para o comando escreva.
     * Strings literais diretas sÃ£o impressas sem formatador extra.
     */
    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext expCtx : ctx.expressao()) {
            String expC = traduzirExpressao(expCtx);
            String expTexto = expCtx.getText();

            if (expTexto.startsWith("\"")) {
                // String literal: printf("texto");
                saida.append(ind()).append("printf(").append(expC).append(");\n");
            } else {
                // VariÃ¡vel ou expressÃ£o: descobre o formatador pelo tipo
                String nomePrincipal = expTexto.replaceAll("\\[.*", "").split("\\.")[0];
                String tipo = tabelaTipos.getOrDefault(nomePrincipal, "");

                if (tipo.equals("real")) {
                    saida.append(ind()).append("printf(\"%f\", ").append(expC).append(");\n");
                } else if (tipo.equals("literal")) {
                    saida.append(ind()).append("printf(\"%s\", ").append(expC).append(");\n");
                } else if (tipo.equals("logico")) {
                    saida.append(ind()).append("printf(\"%d\", ").append(expC).append(");\n");
                } else {
                    saida.append(ind()).append("printf(\"%d\", ").append(expC).append(");\n");
                }
            }
        }
        return null;
    }

    // =========================================================
    // PESSOA 2 â€” Estruturas de controle, operadores, ponteiros
    // =========================================================

    /**
     * se expressao entao ... senao ... fim_se
     * â†’  if (...) { ... } else { ... }
     */
    @Override
    public Void visitCmdSe(LAParser.CmdSeContext ctx) {
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("if (").append(condicao).append(") {\n");

        indent++;
        // Comandos do "entao"
        // Os comandos do bloco se/senao sÃ£o intercalados; precisamos separar pelo
        // Ã­ndice. A gramÃ¡tica coloca todos os cmd* numa lista Ãºnica, com SENAO como
        // separador implÃ­cito. O ANTLR 4 nÃ£o cria sub-listas, mas podemos usar a
        // posiÃ§Ã£o do token SENAO para dividir.
        int posicaoSenao = -1;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("senao")) {
                posicaoSenao = i;
                break;
            }
        }

        // Visita comandos antes do senao
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            int posCmd = ctx.children.indexOf(cmdCtx);
            if (posicaoSenao == -1 || posCmd < posicaoSenao) {
                visit(cmdCtx);
            }
        }
        indent--;

        if (posicaoSenao != -1) {
            saida.append(ind()).append("} else {\n");
            indent++;
            for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
                int posCmd = ctx.children.indexOf(cmdCtx);
                if (posCmd > posicaoSenao) {
                    visit(cmdCtx);
                }
            }
            indent--;
        }

        saida.append(ind()).append("}\n");
        return null;
    }

    /**
     * enquanto expressao faca ... fim_enquanto
     * â†’ while (...) { ... }
     */
    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("while (").append(condicao).append(") {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    /**
     * para IDENT <- inicio ate fim faca ... fim_para
     * â†’ for (IDENT = inicio; IDENT <= fim; IDENT++) { ... }
     */
    @Override
    public Void visitCmdPara(LAParser.CmdParaContext ctx) {
        String var   = ctx.IDENT().getText();
        String inicio = traduzirExpAritmetica(ctx.exp_aritmetica(0));
        String fim    = traduzirExpAritmetica(ctx.exp_aritmetica(1));

        saida.append(ind())
             .append("for (")
             .append(var).append(" = ").append(inicio).append("; ")
             .append(var).append(" <= ").append(fim).append("; ")
             .append(var).append("++")
             .append(") {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    /**
     * faca ... ate expressao
     * â†’ do { ... } while (...);
     */
    @Override
    public Void visitCmdFaca(LAParser.CmdFacaContext ctx) {
        saida.append(ind()).append("do {\n");
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        indent--;
        String condicao = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append("} while (").append(condicao).append(");\n");
        return null;
    }

    /**
     * caso exp_aritmetica seja ... senao ... fim_caso
     * â†’ switch (...) { case ...: ... break; default: ... }
     */
    @Override
    public Void visitCmdCaso(LAParser.CmdCasoContext ctx) {
        String expr = traduzirExpAritmetica(ctx.exp_aritmetica());
        saida.append(ind()).append("switch (").append(expr).append(") {\n");
        indent++;
        visit(ctx.selecao());

        // Bloco senao â†’ default
        // Os cmd do senao ficam nos filhos apÃ³s o token "senao"
        boolean dentroSenao = false;
        boolean temSenao = false;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("senao")) {
                dentroSenao = true;
                temSenao = true;
                break;
            }
        }

        if (temSenao) {
            saida.append(ind()).append("default:\n");
            indent++;
            for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
                visit(cmdCtx);
            }
            saida.append(ind()).append("break;\n");
            indent--;
        }

        indent--;
        saida.append(ind()).append("}\n");
        return null;
    }

    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx) {
        for (LAParser.Item_selecaoContext item : ctx.item_selecao()) {
            visit(item);
        }
        return null;
    }

    @Override
    public Void visitItem_selecao(LAParser.Item_selecaoContext ctx) {
        // Cada item pode ter vÃ¡rios intervalos/valores (1, 3..5, 7)
        for (LAParser.Numero_intervaloContext ni : ctx.constantes().numero_intervalo()) {
            String texto = ni.getText();
            if (texto.contains("..")) {
                // Intervalo: 3..7 â†’ cases 3,4,5,6,7
                String[] partes = texto.split("\\.\\.");
                int inicio = Integer.parseInt(partes[0].replace("-", "").trim());
                int fim    = Integer.parseInt(partes[1].replace("-", "").trim());
                boolean negInicio = partes[0].contains("-");
                boolean negFim    = partes[1].contains("-");
                for (int v = (negInicio ? -inicio : inicio); v <= (negFim ? -fim : fim); v++) {
                    saida.append(ind()).append("case ").append(v).append(":\n");
                }
            } else {
                saida.append(ind()).append("case ").append(texto).append(":\n");
            }
        }
        indent++;
        for (LAParser.CmdContext cmdCtx : ctx.cmd()) {
            visit(cmdCtx);
        }
        saida.append(ind()).append("break;\n");
        indent--;
        return null;
    }

    /**
     * AtribuiÃ§Ã£o: identificador <- expressao
     * Com suporte a ponteiros: ^identificador <- expressao â†’ *ident = expr;
     * â€” Pessoa 2
     */
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String prefixo = (ctx.CIRCUNFLEXO() != null) ? "*" : "";
        String lhs = prefixo + traduzirIdentificador(ctx.identificador());
        String rhs = traduzirExpressao(ctx.expressao());
        saida.append(ind()).append(lhs).append(" = ").append(rhs).append(";\n");
        return null;
    }

    /**
     * Chamada de procedimento: nome(args)
     */
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

    /**
     * retorne expressao â†’ return expressao;
     */
    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        saida.append(ind()).append("return ")
             .append(traduzirExpressao(ctx.expressao())).append(";\n");
        return null;
    }
}

