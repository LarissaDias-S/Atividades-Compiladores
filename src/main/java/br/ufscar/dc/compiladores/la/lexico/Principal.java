package br.ufscar.dc.compiladores.la.lexico;

import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe Principal do Compilador da Linguagem LA — T2.
 *
 * Finalidade: Ponto de entrada do compilador. Coordena o pipeline completo:
 *   1. Leitura do arquivo fonte.
 *   2. Análise Léxica (LALexer) — detecta e reporta erros léxicos.
 *   3. Análise Sintática (LAParser) — detecta e reporta o primeiro erro sintático.
 *   4. Garante que o arquivo de saída SEMPRE termine com "Fim da compilacao".
 *
 * Fluxo de controle de erros:
 *   - Erros léxicos (ERRO_SIMBOLO, ERRO_CADEIA, ERRO_COMENTARIO) são detectados
 *     via pré-varredura dos tokens antes de entregá-los ao Parser.
 *     Ao encontrar um erro léxico, ele é impresso e a análise para.
 *   - Erros sintáticos são detectados pelo CustomErrorListener acoplado ao Parser.
 *   - Em ambos os casos, o fluxo encerra imediatamente após o primeiro erro,
 *     garantindo a mensagem "Fim da compilacao" no final.
 */
public class Principal {

    public static void main(String[] args) {
        // Validação dos argumentos: o compilador precisa de entrada e saída
        if (args.length < 2) {
            System.out.println("Uso: java -jar meu-compilador.jar <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida   = args[1];

        // try-with-resources garante que o PrintWriter sempre seja fechado,
        // o que por sua vez garante o flush do "Fim da compilacao" no disco.
        try (PrintWriter pw = new PrintWriter(arquivoSaida)) {

            // ETAPA 1: Leitura do arquivo fonte
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);

            // ETAPA 2: Instancia o Lexer e cria nosso listener de erros
            LALexer lexer = new LALexer(cs);
            CustomErrorListener errorListener = new CustomErrorListener(pw);

            // ETAPA 3: Pré-varredura léxica para detectar erros ANTES do Parser
            // O ANTLR, por padrão, deixa o Parser consumir os tokens de erro
            // léxico como se fossem tokens comuns. Para interromper no primeiro
            // erro léxico, fazemos uma varredura antecipada dos tokens e os
            // colocamos em uma lista. Se acharmos um erro léxico, imprimimos,
            // encerramos a análise e pulamos direto para o "Fim da compilacao".
            java.util.List<Token> todosOsTokens = new java.util.ArrayList<>();
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

            // Se encontrou erro léxico, encerra aqui (abaixo imprime "Fim da compilacao")
            if (!erroLexico) {
                // Adiciona o token EOF ao final da lista para o Parser funcionar corretamente
                todosOsTokens.add(t); // 't' é o EOF neste ponto

                // ETAPA 4: Monta o CommonTokenStream a partir da lista de tokens e instancia o Parser
                ListTokenSource tokenSource = new ListTokenSource(todosOsTokens);
                CommonTokenStream tokens = new CommonTokenStream(tokenSource);

                LAParser parser = new LAParser(tokens);

                // Remove todos os listeners padrão do ANTLR e adiciona exclusivamente o nosso listener customizado
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);

                // ETAPA 5: Dispara a análise sintática a partir da regra inicial
                parser.programa();
            }

            // ETAPA 6: Mensagem obrigatória de encerramento 
            pw.println("Fim da compilacao");

        } catch (IOException ex) {
            System.err.println("Erro critico de I/O: " + ex.getMessage());
        }
    }
}
