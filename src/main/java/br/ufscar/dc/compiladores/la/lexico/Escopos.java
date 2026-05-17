package br.ufscar.dc.compiladores.la.lexico;

import java.util.Stack;

/**
 * Gerenciador de escopos lexicos do analisador semantico.
 *
 * Mantem uma pilha de TabelaDeSimbolos: ao entrar em um novo bloco
 * empilha-se uma tabela vazia; ao sair, desempilha-se.
 * A busca percorre a pilha do topo a base (escopo mais interno tem prioridade).
 *
 * T4: adicionado controle de escopo de funcao para validar o comando 'retorne'
 * (Erro 5). Uma flag 'dentroFuncao' e empilhada junto com cada escopo para
 * saber se o retorne esta dentro de uma funcao (permitido) ou nao (erro).
 */
public class Escopos {

    /** Pilha de tabelas de simbolos, uma por nivel de escopo */
    private final Stack<TabelaDeSimbolos> pilha = new Stack<>();

    /**
     * T4: Pilha paralela que indica se o escopo correspondente pertence
     * a uma funcao. true = escopo de funcao; false = procedimento ou corpo principal.
     * Mesma estrutura de indices que 'pilha'.
     */
    private final Stack<Boolean> pilhaFuncao = new Stack<>();

    // -------------------------------------------------------------------------
    // Controle de escopo basico
    // -------------------------------------------------------------------------

    /**
     * Empilha novo escopo vazio que NAO pertence a uma funcao.
     * Usar ao entrar no algoritmo principal ou em um procedimento.
     */
    public void entrarEscopo() {
        pilha.push(new TabelaDeSimbolos());
        pilhaFuncao.push(false);
    }

    /**
     * T4: Empilha novo escopo indicando se pertence a uma funcao.
     * Usar ao entrar em declaracao_global para distinguir funcao de procedimento.
     *
     * @param isFuncao true se o escopo que esta sendo aberto e de uma funcao
     */
    public void entrarEscopo(boolean isFuncao) {
        pilha.push(new TabelaDeSimbolos());
        pilhaFuncao.push(isFuncao);
    }

    /** Desempilha o escopo atual. Chamar ao sair de algoritmo/procedimento/funcao. */
    public void sairEscopo() {
        if (!pilha.isEmpty()) {
            pilha.pop();
            pilhaFuncao.pop();
        }
    }

    /** Retorna a tabela do escopo corrente (topo da pilha). */
    public TabelaDeSimbolos escopoAtual() {
        return pilha.peek();
    }

    // -------------------------------------------------------------------------
    // T4: Verificacao do comando 'retorne'
    // -------------------------------------------------------------------------

    /**
     * T4: Verifica se o ponto atual de execucao esta dentro de uma funcao.
     *
     * Percorre a pilha do topo ate a base procurando o primeiro escopo
     * que seja de funcao (true) ou de procedimento/algoritmo (false).
     * Isso permite que o retorne dentro de um bloco 'se' dentro de uma
     * funcao seja considerado valido.
     *
     * @return true se estiver dentro de uma funcao; false caso contrario
     */
    public boolean estaDentroFuncao() {
        for (int i = pilhaFuncao.size() - 1; i >= 0; i--) {
            if (pilhaFuncao.get(i)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Busca na hierarquia de escopos
    // -------------------------------------------------------------------------

    /**
     * Verifica se um identificador existe em QUALQUER escopo visivel.
     * Percorre do topo (mais interno) a base (global).
     */
    public boolean existeNaHierarquia(String nome) {
        for (int i = pilha.size() - 1; i >= 0; i--) {
            if (pilha.get(i).contem(nome)) return true;
        }
        return false;
    }

    /**
     * Recupera o Simbolo percorrendo a hierarquia de escopos.
     * Retorna null se nao encontrado.
     */
    public Simbolo obterNaHierarquia(String nome) {
        for (int i = pilha.size() - 1; i >= 0; i--) {
            if (pilha.get(i).contem(nome)) return pilha.get(i).obter(nome);
        }
        return null;
    }

    /**
     * Verifica se um identificador ja foi declarado NO ESCOPO ATUAL.
     * Usado para detectar redeclaracao no mesmo escopo.
     */
    public boolean existeNoEscopoAtual(String nome) {
        return !pilha.isEmpty() && pilha.peek().contem(nome);
    }

    /** Atalho para adicionar simbolo no escopo atual. */
    public void adicionarNoEscopoAtual(String nome, Simbolo simbolo) {
        pilha.peek().adicionar(nome, simbolo);
    }

    // -------------------------------------------------------------------------
    // Verificacao de compatibilidade de tipos
    // -------------------------------------------------------------------------

    /**
     * Verifica se dois tipos sao compativeis para atribuicao.
     *
     * Regras do T4:
     *   - PONTEIRO <- ENDERECO  (atribuicao de endereco a ponteiro via '&')
     *   - (REAL | INTEIRO) <- (REAL | INTEIRO)
     *   - LITERAL <- LITERAL
     *   - LOGICO <- LOGICO
     *   - REGISTRO <- REGISTRO (mesmo nome de tipo, verificado externamente)
     *   - INDEFINIDO nunca e compativel
     */
    public static boolean verificarCompatibilidade(TipoLA destino, TipoLA origem) {
        if (destino == TipoLA.INDEFINIDO || origem == TipoLA.INDEFINIDO) return false;

        // T4: ponteiro recebe endereco (resultado de '&')
        if (destino == TipoLA.PONTEIRO && origem == TipoLA.ENDERECO) return true;

        // Tipos identicos sao sempre compativeis (exceto REGISTRO, tratado no visitante)
        if (destino == origem) return true;

        // Numericos: inteiro e real sao compativeis entre si
        boolean destinoNumerico = (destino == TipoLA.INTEIRO || destino == TipoLA.REAL);
        boolean origemNumerica  = (origem  == TipoLA.INTEIRO || origem  == TipoLA.REAL);
        if (destinoNumerico && origemNumerica) return true;

        return false;
    }

    /**
     * Determina o tipo resultante de uma operacao aritmetica ou de concatenacao
     * entre parcelas (ex: a + b, s1 * s2).
     *
     * Regras do T4:
     *   - INDEFINIDO em qualquer operando -> INDEFINIDO (sem novo erro aqui)
     *   - INTEIRO com INTEIRO -> INTEIRO; qualquer REAL envolvido -> REAL
     *   - LITERAL com LITERAL -> LITERAL
     *   - LOGICO, PONTEIRO, ENDERECO, REGISTRO ou cruzamentos invalidos -> INDEFINIDO
     *     (ex: literal + logico); o visitante reporta "tipo de expressao incompativel"
     */
    public static TipoLA tipoResultanteExpressao(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.INDEFINIDO || t2 == TipoLA.INDEFINIDO) {
            return TipoLA.INDEFINIDO;
        }

        if ((t1 == TipoLA.INTEIRO || t1 == TipoLA.REAL)
         && (t2 == TipoLA.INTEIRO || t2 == TipoLA.REAL)) {
            return (t1 == TipoLA.REAL || t2 == TipoLA.REAL) ? TipoLA.REAL : TipoLA.INTEIRO;
        }

        if (t1 == TipoLA.LITERAL && t2 == TipoLA.LITERAL) {
            return TipoLA.LITERAL;
        }

        return TipoLA.INDEFINIDO;
    }

    /**
     * Tipo resultante de operadores logicos (e, ou) entre operandos booleanos.
     */
    public static TipoLA tipoResultanteLogico(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.INDEFINIDO || t2 == TipoLA.INDEFINIDO) {
            return TipoLA.INDEFINIDO;
        }
        if (t1 == TipoLA.LOGICO && t2 == TipoLA.LOGICO) {
            return TipoLA.LOGICO;
        }
        return TipoLA.INDEFINIDO;
    }
}
