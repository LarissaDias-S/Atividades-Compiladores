package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.io.PrintWriter;

/**
 * Ouvinte de Erros Customizado para o compilador da Linguagem LA.
 *
 * Intercepta erros sintáticos reportados pelo ANTLR4 e os formata conforme
 * a especificação: "Linha X: erro sintatico proximo a [lexema]"
 * Caso o erro ocorra no fim do arquivo, o lexema é impresso como "EOF".
 */
public class CustomErrorListener extends BaseErrorListener {

    private final PrintWriter saida;
    private boolean erroEncontrado = false;

    public CustomErrorListener(PrintWriter saida) {
        this.saida = saida;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        // Só reporta o primeiro erro; ignora os demais (tentativas de recuperação do ANTLR)
        if (erroEncontrado) {
            return;
        }
        erroEncontrado = true;

        // Cast para Token para extrair apenas o texto do lexema,
        // evitando o toString() que imprime a representação interna do objeto.
        String lexema = "EOF";
        if (offendingSymbol instanceof Token) {
            String texto = ((Token) offendingSymbol).getText();
            if (texto != null && !texto.equals("<EOF>")) {
                lexema = texto;
            }
        }

        saida.println("Linha " + line + ": erro sintatico proximo a " + lexema);
    }

    public boolean isErroEncontrado() {
        return erroEncontrado;
    }

    public void setErroEncontrado() {
        this.erroEncontrado = true;
    }
}
