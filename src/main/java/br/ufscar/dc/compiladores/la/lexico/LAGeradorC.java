package br.ufscar.dc.compiladores.la.lexico;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitador focado na Geração de Código C (Trabalho 5).
 */
public class LAGeradorC extends LAParserBaseVisitor<Void> {

    // StringBuilder para acumular o código C traduzido
    public StringBuilder saida = new StringBuilder();
    
    // Tabela local para guardar os tipos das variáveis e ajudar no printf/scanf
    private Map<String, String> tabelaTipos = new HashMap<>();

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        // --- 1. CABEÇALHO PADRÃO DO C ---
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n\n");
        saida.append("int main() {\n");
        
        // --- 2. VISITA OS FILHOS DA ÁRVORE (Corpo do Algoritmo) ---
        visitChildren(ctx);
        
        // --- 3. ENCERRAMENTO PADRÃO DO C ---
        saida.append("    return 0;\n");
        saida.append("}\n");
        
        return null;
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Tradução de variáveis: inteiro -> int, real -> float, literal -> char[80]
        if (ctx.variavel() != null) {
            String tipoLA = ctx.variavel().tipo().getText();
            String tipoC = "";
            
            if (tipoLA.equals("inteiro")) tipoC = "int";
            else if (tipoLA.equals("real")) tipoC = "float";
            
            for (LAParser.IdentificadorContext idCtx : ctx.variavel().identificador()) {
                String nomeVar = idCtx.getText();
                
                // Salva o tipo na tabela para usar no leia/escreva depois
                tabelaTipos.put(nomeVar, tipoLA);
                
                if (tipoLA.equals("literal")) {
                    // Literais no LA viram vetores de char de 80 posições no C
                    saida.append("    char ").append(nomeVar).append("[80];\n");
                } else if (!tipoC.isEmpty()) {
                    saida.append("    ").append(tipoC).append(" ").append(nomeVar).append(";\n");
                }
            }
        }
        return null;
    }

    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        for (LAParser.IdentificadorContext idCtx : ctx.identificador()) {
            String nomeVar = idCtx.getText();
            String tipo = tabelaTipos.get(nomeVar);
            
            if (tipo != null) {
                if (tipo.equals("inteiro")) {
                    saida.append("    scanf(\"%d\", &").append(nomeVar).append(");\n");
                } else if (tipo.equals("real")) {
                    saida.append("    scanf(\"%f\", &").append(nomeVar).append(");\n");
                } else if (tipo.equals("literal")) {
                    saida.append("    gets(").append(nomeVar).append(");\n");
                }
            }
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        for (LAParser.ExpressaoContext expCtx : ctx.expressao()) {
            String expTexto = expCtx.getText();
            
            // Se for uma string literal direta (ex: " tem ")
            if (expTexto.startsWith("\"")) {
                saida.append("    printf(").append(expTexto).append(");\n");
            } else {
                // Se for uma variável, busca o tipo para saber o formatador
                String tipo = tabelaTipos.get(expTexto);
                if (tipo != null) {
                    if (tipo.equals("inteiro")) {
                        saida.append("    printf(\"%d\", ").append(expTexto).append(");\n");
                    } else if (tipo.equals("real")) {
                        saida.append("    printf(\"%f\", ").append(expTexto).append(");\n");
                    } else if (tipo.equals("literal")) {
                        saida.append("    printf(\"%s\", ").append(expTexto).append(");\n");
                    }
                } else {
                    // Fallback para expressões complexas (Pessoa 2 vai expandir isso depois)
                    saida.append("    printf(\"%d\", ").append(expTexto).append(");\n");
                }
            }
        }
        return null;
    }
}