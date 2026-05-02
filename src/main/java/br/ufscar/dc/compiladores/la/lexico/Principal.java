package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Principal do Compilador da Linguagem LA — T3.
 *
 * Pipeline de compilação:
 *   1. Análise Léxica  — detecta e reporta o primeiro erro léxico
 *   2. Análise Sintática — detecta e reporta o primeiro erro sintático
 *   3. Análise Semântica — detecta e reporta TODOS os erros semânticos
 *   4. Sempre finaliza com "Fim da compilacao"
 *
 * Diferença do T2: a análise semântica NÃO para no primeiro erro;
 * todos os erros encontrados são impressos antes do "Fim da compilacao".
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
            // Percorre os tokens antes de entregar ao Parser para detectar
            // erros léxicos e parar imediatamente no primeiro encontrado.
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

            if (!erroLexico) {
                // Adiciona token EOF para o Parser funcionar corretamente
                todosOsTokens.add(t);

                // ETAPA 3: Análise Sintática
                ListTokenSource tokenSource = new ListTokenSource(todosOsTokens);
                CommonTokenStream tokens    = new CommonTokenStream(tokenSource);
                LAParser parser = new LAParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);

                LAParser.ProgramaContext arvore = parser.programa();

                // ETAPA 4: Análise Semântica — só executa se não houver erro sintático
                // Diferente do T2: imprime TODOS os erros, não apenas o primeiro
                if (!errorListener.isErroEncontrado()) {
                    LASemantico semantico = new LASemantico();
                    semantico.visitPrograma(arvore);

                    for (String erro : semantico.errosSemanticos) {
                        pw.println(erro);
                    }
                }
            }

            // Mensagem obrigatória de encerramento — sempre impressa
            pw.println("Fim da compilacao");

        } catch (IOException ex) {
            System.err.println("Erro critico de I/O: " + ex.getMessage());
        }
    }
}
