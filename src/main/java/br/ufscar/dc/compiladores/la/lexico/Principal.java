package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe principal do Analisador Léxico para a linguagem LA.
 * Responsável por gerenciar a entrada/saída e coordenar o processo de tokenização.
 */
public class Principal {
    public static void main(String[] args) {
        // Verificação de segurança: o programa exige caminho de entrada e de saída
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        // O bloco try-with-resources garante que o arquivo de saída será fechado automaticamente
        try (PrintWriter pw = new PrintWriter(arquivoSaida)) {
            // Cria o fluxo de caracteres a partir do arquivo de entrada
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            
            // Instancia o Lexer gerado pelo ANTLR4
            LALexer lexer = new LALexer(cs);
            Token t = null;

            // Loop principal: percorre o arquivo fonte token por token até o fim 
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                // Obtém o nome simbólico definido na gramática .g4 
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                String textoToken = t.getText();

                // TRATAMENTO DE ERROS LÉXICOS
                // Se encontrar um erro, reporta a linha e o erro específico e interrompe a execução 
                if (nomeToken.equals("ERRO_SIMBOLO")) {
                    pw.println("Linha " + t.getLine() + ": " + textoToken + " - simbolo nao identificado");
                    break; 
                } else if (nomeToken.equals("ERRO_CADEIA")) {
                    pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                    break;
                } else if (nomeToken.equals("ERRO_COMENTARIO")) {
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    break;
                }

                // FORMATAÇÃO DA SAÍDA
                // Tokens especiais (identificadores, cadeias e números) mostram o nome do grupo
                if (nomeToken.equals("IDENT") || nomeToken.equals("CADEIA") || 
                    nomeToken.equals("NUM_INT") || nomeToken.equals("NUM_REAL")) {
                    pw.println("<'" + textoToken + "'," + nomeToken + ">");
                } 
                // Palavras-chave e símbolos fixos mostram o próprio texto duas vezes
                else {
                    pw.println("<'" + textoToken + "','" + textoToken + "'>");
                }
            }
        } catch (IOException ex) {
            // Tratamento de erro caso o arquivo de entrada não seja encontrado ou falhe
            System.err.println("Erro de I/O: " + ex.getMessage());
        }
    }
}