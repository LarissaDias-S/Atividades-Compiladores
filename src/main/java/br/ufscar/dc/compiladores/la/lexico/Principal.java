package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Principal do Compilador da Linguagem LA — T5.
 *
 * Pipeline de compilação:
 * 1. Análise Léxica   — detecta e reporta o primeiro erro léxico.
 * 2. Análise Sintática — detecta e reporta o primeiro erro sintático.
 * 3. Análise Semântica — detecta e reporta TODOS os erros semânticos.
 * 4. Geração de Código — se não houver NENHUM erro, gera o código em C.
 *
 * Atenção ao T5: A frase "Fim da compilacao" SÓ deve ser impressa se 
 * houver erros. Se o código for válido, imprime apenas o código C gerado,
 * caso contrário o GCC falhará ao tentar compilar a string "Fim da compilacao".
 */
public class Principal {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida   = args[1];

        try (PrintWriter pw = new PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(arquivoSaida), java.nio.charset.StandardCharsets.UTF_8))) {

            // ETAPA 1: Leitura do arquivo fonte
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            LALexer lexer = new LALexer(cs);
            CustomErrorListener errorListener = new CustomErrorListener(pw);

            // ETAPA 2: Pré-varredura léxica
            List<Token> todosOsTokens = new ArrayList<>();
            Token t;
            boolean erroLexico = false;

            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                if ("ERRO_SIMBOLO".equals(nomeToken)) {
                    pw.println("Linha " + t.getLine() + ": " + t.getText() + " - simbolo nao identificado");
                    erroLexico = true;
                    break;
                } else if ("ERRO_CADEIA".equals(nomeToken)) {
                    pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                    erroLexico = true;
                    break;
                } else if ("ERRO_COMENTARIO".equals(nomeToken)) {
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    erroLexico = true;
                    break;
                }
                todosOsTokens.add(t);
            }

            // Se encontrou erro léxico, imprime o fim e encerra a compilação
            if (erroLexico) {
                pw.println("Fim da compilacao");
                return;
            }

            todosOsTokens.add(t); // Adiciona EOF

            // ETAPA 3: Análise Sintática
            ListTokenSource tokenSource = new ListTokenSource(todosOsTokens);
            CommonTokenStream tokens    = new CommonTokenStream(tokenSource);
            LAParser parser = new LAParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            LAParser.ProgramaContext arvore = parser.programa();

            // Se encontrou erro sintático, imprime o fim e encerra a compilação
            if (errorListener.isErroEncontrado()) {
                pw.println("Fim da compilacao");
                return;
            }

            // ETAPA 4: Análise Semântica
            LASemantico semantico = new LASemantico();
            semantico.visitPrograma(arvore);

            // Se encontrou erro semântico, imprime os erros, o fim e encerra
            if (!semantico.errosSemanticos.isEmpty()) {
                for (String erro : semantico.errosSemanticos) {
                    pw.println(erro);
                }
                pw.println("Fim da compilacao");
                return;
            }

            // =========================================================
            // ETAPA 5: Geração de Código em C (T5)
            // Se o fluxo chegou até aqui, o código LA não tem nenhum erro!
            // =========================================================
            LAGeradorC gerador = new LAGeradorC();
            gerador.visitPrograma(arvore);
            
            // Grava o código C no arquivo de saída
            pw.print(gerador.saida.toString());

        } catch (IOException ex) {
            System.err.println("Erro critico de I/O: " + ex.getMessage());
        }
    }
}