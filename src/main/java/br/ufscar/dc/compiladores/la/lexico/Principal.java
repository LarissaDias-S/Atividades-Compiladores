package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe Principal do Analisador Léxico.
 * Finalidade: Atuar como ponto de entrada do compilador, realizando o processamento
 * de arquivos e a formatação da saída conforme os requisitos da Linguagem LA.
 */
public class Principal {
    public static void main(String[] args) {
        // Validação dos argumentos de linha de comando:
        // O programa necessita de dois caminhos (entrada e saída) para operar.
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        // Uso de try-with-resources para garantir o fechamento seguro do stream de saída.
        // Isso previne vazamento de memória e arquivos corrompidos.
        try (PrintWriter pw = new PrintWriter(arquivoSaida)) {
            
            // Leitura do arquivo fonte utilizando CharStreams (API do ANTLR4).
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            
            // Instanciação do Lexer gerado a partir da gramática LALexer.g4.
            LALexer lexer = new LALexer(cs);
            Token t = null;

            // Loop de processamento de tokens:
            // O analisador percorre o fluxo até encontrar o token de fim de arquivo.
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                
                // Recupera o nome simbólico do token (ex: IDENT, CADEIA) definido no .g4
                String nomeToken = LALexer.VOCABULARY.getSymbolicName(t.getType());
                String textoToken = t.getText();

                /* * TRATAMENTO DE ERROS LÉXICOS
                 * A especificação exige mensagens customizadas para símbolos inválidos, 
                 * cadeias não fechadas e comentários não finalizados.
                 * Caso um erro seja encontrado, o processo de análise léxica deve ser interrompido.
                 */
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

                /* * FORMATAÇÃO DA SAÍDA DE TOKENS VÁLIDOS
                 * O formato de saída deve seguir o padrão: <'lexema',TIPO_TOKEN>
                 * Diferenciamos tokens de identificação genérica de tokens de texto fixo.
                 */
                if (nomeToken.equals("IDENT") || nomeToken.equals("CADEIA") || 
                    nomeToken.equals("NUM_INT") || nomeToken.equals("NUM_REAL")) {
                    pw.println("<'" + textoToken + "'," + nomeToken + ">");
                } 
                else {
                    // Para palavras-chave e operadores fixos, o tipo do token é o próprio texto.
                    pw.println("<'" + textoToken + "','" + textoToken + "'>");
                }
            }
        } catch (IOException ex) {
            // Tratamento de falhas críticas de acesso ao disco ou arquivos inexistentes.
            System.err.println("Erro crítico de I/O: " + ex.getMessage());
        }
    }
}