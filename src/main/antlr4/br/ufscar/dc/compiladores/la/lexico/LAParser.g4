parser grammar LAParser;

options {
    tokenVocab=LALexer;
}

/**
 * REGRAS SINTÁTICAS PRINCIPAIS
 * Define a estrutura de alto nível de um programa na Linguagem Algorítmica (LA).
 */

/* Ponto de entrada: define a sequência obrigatória de um algoritmo válido */
programa : declaracoes PALAVRA_CHAVE_ALGORITMO corpo PALAVRA_CHAVE_FIM_ALGORITMO EOF ;

/* Agrupamento de definições locais e globais antes do corpo principal */
declaracoes : decl_local_global* ;

decl_local_global : declaracao_local 
                  | declaracao_global 
                  ;

/* Escopo local: variáveis, constantes e definição de novos tipos */
declaracao_local : PALAVRA_CHAVE_DECLARE variavel
                 | 'constante' IDENT DOIS_PONTOS tipo_basico '=' valor_constante
                 | 'tipo' IDENT DOIS_PONTOS tipo
                 ;

/* Definição de variáveis com suporte a listas e tipos */
variavel : identificador (VIRGULA identificador)* DOIS_PONTOS tipo ;

/* Suporte a membros de registros (ponto) e arrays (dimensao) */
identificador : IDENT ('.' IDENT)* dimensao ;

dimensao : ('[' exp_aritmetica ']')* ;

tipo : registro 
     | tipo_estendido 
     ;

/**
 * TIPOS E CONSTANTES
 * Regras para definição de domínios de dados e valores fixos.
 */

tipo_basico : 'literal' | 'inteiro' | 'real' | 'logico' ;

tipo_basico_ident : tipo_basico | IDENT ;

/* Suporte a ponteiros através do símbolo circunflexo */
tipo_estendido : '^'? tipo_basico_ident ;

valor_constante : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso' ;

/* Estrutura de dados composta */
registro : 'registro' variavel* 'fim_registro' ;

/**
 * CORPO E COMANDOS
 * Define o fluxo de execução e a lógica do algoritmo.
 */

corpo : declaracao_local* cmd* ;

/* Conjunto de comandos aceitos pela linguagem */
cmd : cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne ;

cmdLeia : 'leia' '(' '^'? identificador (VIRGULA '^'? identificador)* ')' ;

cmdEscreva : 'escreva' '(' expressao (VIRGULA expressao)* ')' ;

cmdSe : 'se' expressao 'entao' cmd* ('senao' cmd*)? 'fim_se' ;

cmdCaso : 'caso' exp_aritmetica 'seja' selecao ('senao' cmd*)? 'fim_caso' ;

cmdPara : 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' cmd* 'fim_para' ;

cmdEnquanto : 'enquanto' expressao 'faca' cmd* 'fim_enquanto' ;

cmdFaca : 'faca' cmd* 'ate' expressao ;

cmdAtribuicao : '^'? identificador '<-' expressao ;

cmdChamada : IDENT '(' expressao (VIRGULA expressao)* ')' ;

cmdRetorne : 'retorne' expressao ;

/* Estruturas auxiliares para o comando 'caso' */
selecao : item_selecao* ;

item_selecao : constantes DOIS_PONTOS cmd* ;

constantes : numero_intervalo (VIRGULA numero_intervalo)* ;

numero_intervalo : op_unario? NUM_INT ('..' op_unario? NUM_INT)? ;

/**
 * DECLARAÇÕES GLOBAIS
 * Definição de sub-rotinas: procedimentos (sem retorno) e funções (com retorno).
 */

declaracao_global : 'procedimento' IDENT '(' parametros? ')' declaracao_local* cmd* 'fim_procedimento'
                  | 'funcao' IDENT '(' parametros? ')' DOIS_PONTOS tipo_estendido declaracao_local* cmd* 'fim_funcao'
                  ;

parametros : parametro (VIRGULA parametro)* ;

parametro : 'var'? identificador (VIRGULA identificador)* DOIS_PONTOS tipo_estendido ;

/**
 * EXPRESSÕES ARITMÉTICAS, RELACIONAIS E LÓGICAS
 * Organizadas por níveis de precedência (aritmética > relacional > lógica).
 */

/* Nível 1: Soma e Subtração */
exp_aritmetica : termo (op1 termo)* ;

/* Nível 2: Multiplicação e Divisão */
termo : fator (op2 fator)* ;

/* Nível 3: Módulo */
fator : parcela (op3 parcela)* ;

op1 : '+' | '-' ;

op2 : '*' | '/' ;

op3 : '%' ;

/* Unidades básicas de uma expressão aritmética */
parcela : op_unario? parcela_unario 
        | parcela_nao_unario 
        ;

parcela_unario : '^'? identificador 
               | IDENT '(' expressao (VIRGULA expressao)* ')' 
               | NUM_INT 
               | NUM_REAL 
               | '(' expressao ')' 
               ;

parcela_nao_unario : '&' identificador 
                   | CADEIA 
                   ;

/* Comparações entre expressões aritméticas */
exp_relacional : exp_aritmetica (op_relacional exp_aritmetica)? ;

op_relacional : '=' | '<>' | '>=' | '<=' | '>' | '<' ;

/* Lógica de primeira ordem: ou, e, nao */
expressao : termo_logico (op_logico_1 termo_logico)* ;

termo_logico : fator_logico (op_logico_2 fator_logico)* ;

fator_logico : 'nao'? parcela_logica ;

parcela_logica : 'verdadeiro' 
               | 'falso' 
               | exp_relacional 
               ;

op_logico_1 : 'ou' ;

op_logico_2 : 'e' ;

op_unario : '-' ;