package br.ufscar.dc.compiladores.la.lexico;

import java.util.Stack;

/**
 * Gerenciador de escopos léxicos do analisador semântico.
 *
 * Mantém uma pilha de TabelaDeSimbolos: ao entrar em um novo bloco
 * empilha-se uma tabela vazia; ao sair, desempilha-se.
 * A busca percorre a pilha do topo à base (escopo mais interno tem prioridade).
 */
public class Escopos {

    /** Pilha de tabelas de símbolos, uma por nível de escopo */
    private final Stack<TabelaDeSimbolos> pilha = new Stack<>();

    /** Empilha novo escopo vazio. Chamar ao entrar em algoritmo/procedimento/função. */
    public void entrarEscopo() {
        pilha.push(new TabelaDeSimbolos());
    }

    /** Desempilha o escopo atual. Chamar ao sair de algoritmo/procedimento/função. */
    public void sairEscopo() {
        if (!pilha.isEmpty()) pilha.pop();
    }

    /** Retorna a tabela do escopo corrente (topo da pilha). */
    public TabelaDeSimbolos escopoAtual() {
        return pilha.peek();
    }

    /**
     * Verifica se um identificador existe em QUALQUER escopo visível.
     * Percorre do topo (mais interno) à base (global).
     */
    public boolean existeNaHierarquia(String nome) {
        for (int i = pilha.size() - 1; i >= 0; i--) {
            if (pilha.get(i).contem(nome)) return true;
        }
        return false;
    }

    /**
     * Recupera o Simbolo percorrendo a hierarquia de escopos.
     * Retorna null se não encontrado.
     */
    public Simbolo obterNaHierarquia(String nome) {
        for (int i = pilha.size() - 1; i >= 0; i--) {
            if (pilha.get(i).contem(nome)) return pilha.get(i).obter(nome);
        }
        return null;
    }

    /**
     * Verifica se um identificador já foi declarado NO ESCOPO ATUAL.
     * Usado para detectar redeclaração no mesmo escopo.
     */
    public boolean existeNoEscopoAtual(String nome) {
        return !pilha.isEmpty() && pilha.peek().contem(nome);
    }

    /** Atalho para adicionar símbolo no escopo atual. */
    public void adicionarNoEscopoAtual(String nome, Simbolo simbolo) {
        pilha.peek().adicionar(nome, simbolo);
    }

    /**
     * Verifica se dois tipos são compatíveis para atribuição ou expressão.
     *
     * Regras do T3:
     *   - PONTEIRO <- PONTEIRO
     *   - (REAL | INTEIRO) <- (REAL | INTEIRO)
     *   - LITERAL <- LITERAL
     *   - LOGICO <- LOGICO
     *   - REGISTRO <- REGISTRO (mesmo nome de tipo, verificado externamente)
     *   - INDEFINIDO nunca é compatível
     */
    public static boolean verificarCompatibilidade(TipoLA destino, TipoLA origem) {
        if (destino == TipoLA.INDEFINIDO || origem == TipoLA.INDEFINIDO) return false;
        if (destino == origem) return true;

        boolean destinoNumerico = (destino == TipoLA.INTEIRO || destino == TipoLA.REAL);
        boolean origemNumerica  = (origem  == TipoLA.INTEIRO || origem  == TipoLA.REAL);
        if (destinoNumerico && origemNumerica) return true;

        return false;
    }

    /**
     * Determina o tipo resultante de uma expressão binária (ex: a + b).
     *
     * Regras:
     *   - INDEFINIDO em qualquer operando -> INDEFINIDO
     *   - INTEIRO op INTEIRO -> INTEIRO
     *   - qualquer numérico com REAL -> REAL
     *   - LOGICO op LOGICO -> LOGICO
     *   - qualquer outro cruzamento -> INDEFINIDO
     */
    public static TipoLA tipoResultanteExpressao(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.INDEFINIDO || t2 == TipoLA.INDEFINIDO) return TipoLA.INDEFINIDO;

        if ((t1 == TipoLA.INTEIRO || t1 == TipoLA.REAL) && (t2 == TipoLA.INTEIRO || t2 == TipoLA.REAL))
            return (t1 == TipoLA.REAL || t2 == TipoLA.REAL) ? TipoLA.REAL : TipoLA.INTEIRO;

        if (t1 == TipoLA.LOGICO && t2 == TipoLA.LOGICO) return TipoLA.LOGICO;

        return TipoLA.INDEFINIDO;
    }
}
