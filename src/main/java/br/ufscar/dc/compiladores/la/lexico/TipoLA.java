package br.ufscar.dc.compiladores.la.lexico;

/**
 * Enumeração dos tipos primitivos e compostos da Linguagem Algorítmica (LA).
 *
 * Cada constante representa um domínio de dados reconhecido pelo analisador
 * semântico. O valor INDEFINIDO é usado como sentinela quando uma expressão
 * contém tipos incompatíveis ou quando um identificador não foi declarado,
 * impedindo a propagação de falsos erros nas verificações subsequentes.
 */
public enum TipoLA {

    /** Números inteiros (ex: 42, -7) */
    INTEIRO,

    /** Números reais/ponto-flutuante (ex: 3.14, -0.5) */
    REAL,

    /** Valores lógicos: verdadeiro ou falso */
    LOGICO,

    /** Cadeias de caracteres entre aspas (ex: "olá") */
    LITERAL,

    /** Referência de memória, variável declarada com '^' antes do tipo */
    PONTEIRO,

    /**
     * Tipo estruturado composto por campos (declarado com 'registro ... fim_registro').
     * A compatibilidade entre registros exige que ambos tenham o mesmo nome de tipo.
     */
    REGISTRO,

    /**
     * Sentinela de erro: atribuído quando um tipo não pode ser determinado.
     * Evita a cascata de erros falsos ao continuar a análise após um erro real.
     */
    INDEFINIDO
}
