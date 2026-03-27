lexer grammar LALexer;

// ---------------------------------------------------------
// Palavras-chave reservadas da linguagem LA
// Declaradas no topo para precedência sobre os Identificadores
// ---------------------------------------------------------
PALAVRA_CHAVE_ALGORITMO     : 'algoritmo';
PALAVRA_CHAVE_DECLARE       : 'declare';
PALAVRA_CHAVE_INTEIRO       : 'inteiro';
PALAVRA_CHAVE_LITERAL       : 'literal';
PALAVRA_CHAVE_LEIA          : 'leia';
PALAVRA_CHAVE_ESCREVA       : 'escreva';
PALAVRA_CHAVE_FIM_ALGORITMO : 'fim_algoritmo';

// ---------------------------------------------------------
// Símbolos especiais e pontuação
// ---------------------------------------------------------
DOIS_PONTOS : ':';
ABRE_PAR    : '(';
FECHA_PAR   : ')';
VIRGULA     : ',';

// ---------------------------------------------------------
// Tipos complexos
// ---------------------------------------------------------

// Identificadores: obrigatoriamente iniciam com letra, podendo conter números e sublinhado
IDENT : [a-zA-Z][a-zA-Z0-9_]* ;

// Cadeia literal: sequência de caracteres delimitada por aspas duplas
CADEIA : '"' ~'"'* '"' ;


// ---------------------------------------------------------
// Descarte (Tokens ignorados na análise sintática)
// ---------------------------------------------------------

// Espaços em branco, tabulações e quebras de linha
WS : [ \t\r\n]+ -> skip ;

// Comentários de bloco delimitados por chaves
COMENTARIO : '{' ~'}'* '}' -> skip ;


// ---------------------------------------------------------
// Captura de Erros Léxicos
// Regras de *fallback* posicionadas no final da gramática
// ---------------------------------------------------------

// Captura aspas abertas que atingem uma quebra de linha sem fechar
ERRO_CADEIA : '"' ~['"\r\n]* ;

// Captura chaves abertas que atingem o fim do arquivo (EOF) sem fechar
ERRO_COMENTARIO : '{' ~'}'* EOF ;

// Captura qualquer caractere inválido que não casou com as regras anteriores
ERRO_SIMBOLO : . ;