parser grammar LAParser;

options {
    tokenVocab=LALexer;
}

/**
 * REGRAS SINTÁTICAS PRINCIPAIS
 */

programa : declaracoes PALAVRA_CHAVE_ALGORITMO corpo PALAVRA_CHAVE_FIM_ALGORITMO EOF ;

declaracoes : decl_local_global* ;

decl_local_global : declaracao_local 
                  | declaracao_global 
                  ;

declaracao_local : PALAVRA_CHAVE_DECLARE variavel
                 | PALAVRA_CHAVE_CONSTANTE IDENT DOIS_PONTOS tipo_basico IGUAL valor_constante
                 | PALAVRA_CHAVE_TIPO IDENT DOIS_PONTOS tipo
                 ;

variavel : identificador (VIRGULA identificador)* DOIS_PONTOS tipo ;

identificador : IDENT (PONTO IDENT)* dimensao ;

dimensao : (ABRE_COL exp_aritmetica FECHA_COL)* ;

tipo : registro | tipo_estendido ;

/**
 * TIPOS E CONSTANTES
 */

tipo_basico : PALAVRA_CHAVE_LITERAL 
            | PALAVRA_CHAVE_INTEIRO 
            | PALAVRA_CHAVE_REAL 
            | PALAVRA_CHAVE_LOGICO 
            ;

tipo_basico_ident : tipo_basico | IDENT ;

tipo_estendido : CIRCUNFLEXO? tipo_basico_ident ;

valor_constante : CADEIA | NUM_INT | NUM_REAL | PALAVRA_CHAVE_VERDADEIRO | PALAVRA_CHAVE_FALSO ;

registro : PALAVRA_CHAVE_REGISTRO variavel* PALAVRA_CHAVE_FIM_REGISTRO ;

/**
 * CORPO E COMANDOS
 */

corpo : declaracao_local* cmd* ;

cmd : cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne ;

cmdLeia : PALAVRA_CHAVE_LEIA ABRE_PAR CIRCUNFLEXO? identificador (VIRGULA CIRCUNFLEXO? identificador)* FECHA_PAR ;

cmdEscreva : PALAVRA_CHAVE_ESCREVA ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR ;

cmdSe : PALAVRA_CHAVE_SE expressao PALAVRA_CHAVE_ENTAO cmd* (PALAVRA_CHAVE_SENAO cmd*)? PALAVRA_CHAVE_FIM_SE ;

cmdCaso : PALAVRA_CHAVE_CASO exp_aritmetica PALAVRA_CHAVE_SEJA selecao (PALAVRA_CHAVE_SENAO cmd*)? PALAVRA_CHAVE_FIM_CASO ;

cmdPara : PALAVRA_CHAVE_PARA IDENT ATRIBUICAO exp_aritmetica PALAVRA_CHAVE_ATE exp_aritmetica PALAVRA_CHAVE_FACA cmd* PALAVRA_CHAVE_FIM_PARA ;

cmdEnquanto : PALAVRA_CHAVE_ENQUANTO expressao PALAVRA_CHAVE_FACA cmd* PALAVRA_CHAVE_FIM_ENQUANTO ;

cmdFaca : PALAVRA_CHAVE_FACA cmd* PALAVRA_CHAVE_ATE expressao ;

cmdAtribuicao : CIRCUNFLEXO? identificador ATRIBUICAO expressao ;

cmdChamada : IDENT ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR ;

cmdRetorne : PALAVRA_CHAVE_RETORNE expressao ;

selecao : item_selecao* ;

item_selecao : constantes DOIS_PONTOS cmd* ;

constantes : numero_intervalo (VIRGULA numero_intervalo)* ;

numero_intervalo : op_unario? NUM_INT (PONTEIROS op_unario? NUM_INT)? ;

/**
 * DECLARAÇÕES GLOBAIS
 */

declaracao_global : PALAVRA_CHAVE_PROCEDIMENTO IDENT ABRE_PAR parametros? FECHA_PAR declaracao_local* cmd* PALAVRA_CHAVE_FIM_PROCEDIMENTO
                  | PALAVRA_CHAVE_FUNCAO IDENT ABRE_PAR parametros? FECHA_PAR DOIS_PONTOS tipo_estendido declaracao_local* cmd* PALAVRA_CHAVE_FIM_FUNCAO
                  ;

parametros : parametro (VIRGULA parametro)* ;

parametro : PALAVRA_CHAVE_VAR? identificador (VIRGULA identificador)* DOIS_PONTOS tipo_estendido ;

/**
 * EXPRESSÕES
 */

exp_aritmetica : termo (op1 termo)* ;

termo : fator (op2 fator)* ;

fator : parcela (op3 parcela)* ;

op1 : MAIS | MENOS ;

op2 : VEZES | DIVIDIDO ;

op3 : MODULO ;

parcela : op_unario? parcela_unario 
        | parcela_nao_unario 
        ;

parcela_unario : CIRCUNFLEXO? identificador 
               | IDENT ABRE_PAR expressao (VIRGULA expressao)* FECHA_PAR 
               | NUM_INT 
               | NUM_REAL 
               | ABRE_PAR expressao FECHA_PAR 
               ;

parcela_nao_unario : AMPERSAND identificador 
                   | CADEIA 
                   ;

exp_relacional : exp_aritmetica (op_relacional exp_aritmetica)? ;

op_relacional : IGUAL | DIFERENTE | MAIOR_IGUAL | MENOR_IGUAL | MAIOR | MENOR ;

expressao : termo_logico (op_logico_1 termo_logico)* ;

termo_logico : fator_logico (op_logico_2 fator_logico)* ;

fator_logico : PALAVRA_CHAVE_NAO? parcela_logica ;

parcela_logica : PALAVRA_CHAVE_VERDADEIRO 
               | PALAVRA_CHAVE_FALSO 
               | exp_relacional 
               ;

op_logico_1 : PALAVRA_CHAVE_OU ;

op_logico_2 : PALAVRA_CHAVE_E ;

op_unario : MENOS ;