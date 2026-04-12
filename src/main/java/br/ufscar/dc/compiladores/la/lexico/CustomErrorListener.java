package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import java.io.PrintWriter;

/**
 * CustomErrorListener provê um mecanismo personalizado de tratamento de erros sintáticos.
 * * Esta classe estende o BaseErrorListener do ANTLR4 para interceptar eventos de erro
 * e formatar a saída de acordo com os requisitos específicos da Linguagem LA:
 * "Linha X: erro sintatico proximo a [lexema]".
 */
public class CustomErrorListener extends BaseErrorListener {

    private final PrintWriter saida;
    private boolean erroEncontrado = false;

    /**
     * Construtor que recebe o PrintWriter para escrita no arquivo de saída.
     * * @param saida O fluxo de saída onde as mensagens de erro serão gravadas.
     */
    public CustomErrorListener(PrintWriter saida) {
        this.saida = saida;
    }

    /**
     * Método invocado automaticamente pelo ANTLR quando um erro sintático é detectado.
     * * Implementa a lógica de interrupção no primeiro erro, ignorando tentativas de 
     * recuperação subsequentes para manter a precisão do reporte exigido.
     */
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        /* * Lógica de interrupção: O ANTLR tenta se recuperar de erros sintáticos 
         * por padrão. Para a especificação do Trabalho 2, reportamos apenas a 
         * primeira ocorrência.
         */
        if (erroEncontrado) {
            return;
        }
        erroEncontrado = true;

        /*
         * Extração do lexema causador do erro. Caso o símbolo seja nulo ou
         * represente o fim do arquivo (<EOF>), o texto é normalizado para "EOF".
         */
        String lexema = "EOF";
        if (offendingSymbol instanceof Token) {
            String texto = ((Token) offendingSymbol).getText();
            if (texto != null && !texto.equals("<EOF>")) {
                lexema = texto;
            }
        }

        saida.println("Linha " + line + ": erro sintatico proximo a " + lexema);
    }

    /**
     * Verifica se algum erro foi detectado durante a análise.
     * * @return true se um erro sintático foi reportado, false caso contrário.
     */
    public boolean isErroEncontrado() {
        return erroEncontrado;
    }

    /**
     * Força o estado de erro encontrado.
     * Útil para sincronizar estados entre analisadores léxicos e sintáticos.
     */
    public void setErroEncontrado() {
        this.erroEncontrado = true;
    }
}