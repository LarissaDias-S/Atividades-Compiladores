parser grammar LAParser;

options {
    tokenVocab=LALexer;
}

// ---------------------------------------------------------
// REGRAS SINTÁTICAS
// ---------------------------------------------------------

// programa -> declaracoes "algoritmo" corpo "fim_algoritmo"
programa : declaracoes PALAVRA_CHAVE_ALGORITMO corpo PALAVRA_CHAVE_FIM_ALGORITMO EOF ;

// declaracoes -> {decl_local_global}
declaracoes : decl_local_global* ;

// decl_local_global -> declaracao_local | declaracao_global
decl_local_global : declaracao_local 
                  | declaracao_global 
                  ;

// declaracao_local -> "declare" variavel | "constante" IDENT ":" tipo_basico "=" valor_constante | "tipo" IDENT ":" tipo
declaracao_local : PALAVRA_CHAVE_DECLARE variavel
                 | 'constante' IDENT DOIS_PONTOS tipo_basico '=' valor_constante
                 | 'tipo' IDENT DOIS_PONTOS tipo
                 ;

// variavel -> identificador {"," identificador} ":" tipo
variavel : identificador (VIRGULA identificador)* DOIS_PONTOS tipo ;

// identificador -> IDENT {"." IDENT} dimensão
identificador : IDENT ('.' IDENT)* dimensao ;

// dimensao -> {"[" exp_aritmetica "]"}
dimensao : ('[' exp_aritmetica ']')* ;

// tipo -> registro | tipo_estendido
tipo : registro 
     | tipo_estendido 
     ;

// ---------------------------------------------------------
// TIPOS E CONSTANTES
// ---------------------------------------------------------
tipo_basico : 'literal' | 'inteiro' | 'real' | 'logico' ;

tipo_basico_ident : tipo_basico | IDENT ;

tipo_estendido : '^'? tipo_basico_ident ;

valor_constante : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso' ;

registro : 'registro' variavel* 'fim_registro' ;


// ---------------------------------------------------------
// CORPO E COMANDOS
// ---------------------------------------------------------
corpo : declaracao_local* cmd* ;

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

selecao : item_selecao* ;

item_selecao : constantes DOIS_PONTOS cmd* ;

constantes : numero_intervalo (VIRGULA numero_intervalo)* ;

numero_intervalo : op_unario? NUM_INT ('..' op_unario? NUM_INT)? ;


// ---------------------------------------------------------
// DECLARAÇÕES GLOBAIS (Procedimentos e Funções)
// ---------------------------------------------------------
declaracao_global : 'procedimento' IDENT '(' parametros? ')' declaracao_local* cmd* 'fim_procedimento'
                  | 'funcao' IDENT '(' parametros? ')' DOIS_PONTOS tipo_estendido declaracao_local* cmd* 'fim_funcao'
                  ;

parametros : parametro (VIRGULA parametro)* ;

parametro : 'var'? identificador (VIRGULA identificador)* DOIS_PONTOS tipo_estendido ;

// ---------------------------------------------------------
// EXPRESSÕES ARITMÉTICAS, RELACIONAIS E LÓGICAS
// ---------------------------------------------------------

exp_aritmetica : termo (op1 termo)* ;

termo : fator (op2 fator)* ;

fator : parcela (op3 parcela)* ;

op1 : '+' | '-' ;

op2 : '*' | '/' ;

op3 : '%' ;

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

exp_relacional : exp_aritmetica (op_relacional exp_aritmetica)? ;

op_relacional : '=' | '<>' | '>=' | '<=' | '>' | '<' ;

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