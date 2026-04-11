lexer grammar LALexer;

// Palavras-chave reservadas - declaradas antes de IDENT para garantir precedencia
PALAVRA_CHAVE_ALGORITMO        : 'algoritmo';
PALAVRA_CHAVE_FIM_ALGORITMO    : 'fim_algoritmo';
PALAVRA_CHAVE_DECLARE          : 'declare';
PALAVRA_CHAVE_INTEIRO          : 'inteiro';
PALAVRA_CHAVE_REAL             : 'real';
PALAVRA_CHAVE_LOGICO           : 'logico';
PALAVRA_CHAVE_LITERAL          : 'literal';
PALAVRA_CHAVE_LEIA             : 'leia';
PALAVRA_CHAVE_ESCREVA          : 'escreva';
PALAVRA_CHAVE_SE               : 'se';
PALAVRA_CHAVE_ENTAO            : 'entao';
PALAVRA_CHAVE_SENAO            : 'senao';
PALAVRA_CHAVE_FIM_SE           : 'fim_se';
PALAVRA_CHAVE_ENQUANTO         : 'enquanto';
PALAVRA_CHAVE_FACA             : 'faca';
PALAVRA_CHAVE_FIM_ENQUANTO     : 'fim_enquanto';
PALAVRA_CHAVE_PARA             : 'para';
PALAVRA_CHAVE_ATE              : 'ate';
PALAVRA_CHAVE_FIM_PARA         : 'fim_para';
PALAVRA_CHAVE_RETORNE          : 'retorne';
PALAVRA_CHAVE_E                : 'e';
PALAVRA_CHAVE_OU               : 'ou';
PALAVRA_CHAVE_NAO              : 'nao';
PALAVRA_CHAVE_VERDADEIRO       : 'verdadeiro';
PALAVRA_CHAVE_FALSO            : 'falso';
PALAVRA_CHAVE_TIPO             : 'tipo';
PALAVRA_CHAVE_REGISTRO         : 'registro';
PALAVRA_CHAVE_FIM_REGISTRO     : 'fim_registro';
PALAVRA_CHAVE_PROCEDIMENTO     : 'procedimento';
PALAVRA_CHAVE_FIM_PROCEDIMENTO : 'fim_procedimento';
PALAVRA_CHAVE_FUNCAO           : 'funcao';
PALAVRA_CHAVE_FIM_FUNCAO       : 'fim_funcao';
PALAVRA_CHAVE_VAR              : 'var';
PALAVRA_CHAVE_CONSTANTE        : 'constante';
PALAVRA_CHAVE_CASO             : 'caso';
PALAVRA_CHAVE_SEJA             : 'seja';
PALAVRA_CHAVE_FIM_CASO         : 'fim_caso';
PONTEIROS                      : '..';

// Operadores aritmeticos e de atribuicao
ATRIBUICAO : '<-';
MAIS       : '+';
MENOS      : '-';
VEZES      : '*';
DIVIDIDO   : '/';
MODULO     : '%';

// Operadores relacionais - os compostos antes dos simples
MENOR_IGUAL : '<=';
MAIOR_IGUAL : '>=';
DIFERENTE   : '<>';
MENOR       : '<';
MAIOR       : '>';
IGUAL       : '=';

// Simbolos especiais e pontuacao
DOIS_PONTOS : ':';
PONTO       : '.';
ABRE_PAR    : '(';
FECHA_PAR   : ')';
ABRE_COL    : '[';
FECHA_COL   : ']';
VIRGULA     : ',';
CIRCUNFLEXO : '^';
AMPERSAND   : '&';

// Literais numericos - NUM_REAL antes de NUM_INT para ter precedencia
NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT : [0-9]+ ;

// Identificadores e cadeias
IDENT  : [a-zA-Z][a-zA-Z0-9_]* ;
CADEIA : '"' ~["\r\n]* '"' ;

// Descarte
WS         : [ \t\r\n]+ -> skip ;
COMENTARIO      : '{' ~[}\r\n]* '}' -> skip ;

// Erros lexicos
ERRO_CADEIA     : '"' ~["\r\n]* ;
ERRO_COMENTARIO : '{' ~[}\r\n]* ;
ERRO_SIMBOLO    : . ;
