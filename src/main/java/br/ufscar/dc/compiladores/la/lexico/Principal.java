package br.ufscar.dc.compiladores.la.lexico;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.io.PrintWriter;

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
            Token t = null;
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                String textoToken = t.getText();
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
                if (nomeToken.equals("IDENT") || nomeToken.equals("CADEIA") || nomeToken.equals("NUM_INT") || nomeToken.equals("NUM_REAL")) {
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