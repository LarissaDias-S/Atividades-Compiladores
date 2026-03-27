package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.io.PrintWriter;

public class Principal {
    public static void main(String[] args) {
        // Valida se os argumentos foram passados (entrada e saída)
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter pw = new PrintWriter(arquivoSaida)) {
            // Lê o arquivo de entrada
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            
            // Instancia o Lexer maravilhoso que você construiu no .g4
            LALexer lexer = new LALexer(cs);

            Token t = null;
            // Laço de repetição que pede tokens até o fim do arquivo
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                String textoToken = t.getText();

                // Tratamento dos Erros Léxicos
                if (nomeToken.equals("ERRO_SIMBOLO")) {
                    pw.println("Linha " + t.getLine() + ": " + textoToken + " - simbolo nao identificado");
                    break;
                } 
                else if (nomeToken.equals("ERRO_CADEIA")) {
                    pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                    break;
                } 
                else if (nomeToken.equals("ERRO_COMENTARIO")) {
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    break;
                }

                // Tokens Válidos (O Colega 2 pode melhorar essa parte depois se quiser!)
                if (nomeToken.equals("IDENT") || nomeToken.equals("CADEIA")) {
                    pw.println("<'" + textoToken + "'," + nomeToken + ">");
                } else {
                    pw.println("<'" + textoToken + "','" + textoToken + "'>");
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro: " + ex.getMessage());
        }
    }
}