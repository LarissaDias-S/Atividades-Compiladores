package br.ufscar.dc.compiladores.la.lexico;

/**
 * Enumeracao dos tipos primitivos e compostos da Linguagem Algoritmica (LA).
 *
 * Cada constante representa um dominio de dados reconhecido pelo analisador
 * semantico. O valor INDEFINIDO e usado como sentinela quando uma expressao
 * contem tipos incompativeis ou quando um identificador nao foi declarado,
 * impedindo a propagacao de falsos erros nas verificacoes subsequentes.
 *
 * T4: adicionado ENDERECO para representar o resultado do operador '&'
 * (diferente de PONTEIRO, que e o tipo de uma variavel declarada com '^').
 * Isso permite checar a regra: ponteiro <- endereco.
 */
public enum TipoLA {

    /** Numeros inteiros (ex: 42, -7) */
    INTEIRO,

    /** Numeros reais/ponto-flutuante (ex: 3.14, -0.5) */
    REAL,

    /** Valores logicos: verdadeiro ou falso */
    LOGICO,

    /** Cadeias de caracteres entre aspas (ex: "ola") */
    LITERAL,

    /** Variavel declarada com '^' antes do tipo (ex: ^inteiro) */
    PONTEIRO,

    /**
     * Resultado do operador '&' aplicado a um identificador (ex: &x).
     * Compativel apenas com destinos do tipo PONTEIRO.
     * T4: necessario para distinguir ponteiro (tipo da variavel) de
     * endereco (valor produzido por '&').
     */
    ENDERECO,

    /**
     * Tipo estruturado composto por campos (declarado com 'registro ... fim_registro').
     * A compatibilidade entre registros exige que ambos tenham o mesmo nome de tipo.
     */
    REGISTRO,

    /**
     * Sentinela de erro: atribuido quando um tipo nao pode ser determinado.
     * Evita a cascata de erros falsos ao continuar a analise apos um erro real.
     */
    INDEFINIDO
}
