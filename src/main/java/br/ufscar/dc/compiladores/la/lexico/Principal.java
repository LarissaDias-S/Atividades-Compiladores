package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Principal {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter pw = new PrintWriter(arquivoSaida)) {
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            LALexer lexer = new LALexer(cs);
            
            // O ErrorListener deve receber o PrintWriter para escrever no arquivo correto
            CustomErrorListener errorListener = new CustomErrorListener(pw);

            List<Token> todosOsTokens = new ArrayList<>();
            Token t;
            boolean erroLexico = false;

            // ETAPA 1: Análise Léxica
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
                todosOsTokens.add(lexer.nextToken()); // Garante a adição do EOF correto
                ListTokenSource tokenSource = new ListTokenSource(todosOsTokens);
                CommonTokenStream tokens = new CommonTokenStream(tokenSource);
                LAParser parser = new LAParser(tokens);

                // ETAPA 2: Análise Sintática
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);
                
                // AJUSTE 1: Rodar o parser
                LAParser.ProgramaContext arvore = parser.programa();

                // ETAPA 3: Análise Semântica (Seu foco como Pessoa 2)
                // AJUSTE 2: Só entra aqui se o Sintático não achou erro estrutural
                if (!errorListener.isErroEncontrado()) {
                    LASemantico semantico = new LASemantico();
                    semantico.visitPrograma(arvore);

                    // AJUSTE 3: Imprime apenas o PRIMEIRO erro semântico (padrão da disciplina)
                    // Se quiser imprimir todos, mantenha o seu loop 'for'
                    if (!semantico.errosSemanticos.isEmpty()) {
                        pw.println(semantico.errosSemanticos.get(0));
                    }
                }
            }

            pw.println("Fim da compilacao");

        } catch (IOException ex) {
            System.err.println("Erro critico de I/O: " + ex.getMessage());
        }
    }
}