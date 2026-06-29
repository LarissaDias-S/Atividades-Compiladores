/**
 * MLDeclara.g4 — Gramática Combinada (Léxica + Sintática) da Linguagem ML-Declara
 *
 * Linguagem declarativa para treinamento rápido de modelos de Machine Learning.
 * Disciplina: Construção de Compiladores — Prof. Daniel Lucrédio | UFSCar
 * Grupo: Bruna Matias, Julia Pedro, Larissa Dias
 *
 * Estrutura de um programa .mld:
 *
 *   DATASET "caminho.csv"
 *     COLUMNS col1, col2, col3
 *   END
 *
 *   TARGET_VAR alvo
 *   FEATURES col1, col2
 *
 *   MODEL meuModelo RandomForest
 *     n_estimators = 100
 *     max_depth    = 5
 *   END
 *
 *   METRICS accuracy, f1_score
 *   OUTPUT "modelo_salvo.pkl"
 *
 * Algoritmos aceitos: RandomForest, XGBoost, LinearRegression,
 *                     LogisticRegression, SVM
 * Métricas aceitas  : accuracy, f1_score, precision, recall,
 *                     RMSE, MAE, R2
 */
grammar MLDeclara;

// ============================================================
//  REGRA INICIAL
// ============================================================

/**
 * programa: ponto de entrada — um programa MLDeclara é composto por
 * um bloco DATASET, as diretivas TARGET_VAR e FEATURES (em qualquer
 * ordem entre si), um ou mais blocos MODEL, e as diretivas METRICS e
 * OUTPUT, seguidos de EOF.
 */
programa
    : blocoDataset
      diretiva+
      blocoModel+
      diretivaMétrics
      diretívaOutput
      EOF
    ;

// ============================================================
//  BLOCO DATASET
// ============================================================

/**
 * blocoDataset: declara o caminho do arquivo CSV e a lista de
 * colunas disponíveis no dataset.
 *
 * Exemplo:
 *   DATASET "dados.csv"
 *     COLUMNS idade, salario, regiao, comprou
 *   END
 */
blocoDataset
    : KW_DATASET STRING
      KW_COLUMNS listaColunasDataset
      KW_END
    ;

/**
 * listaColunasDataset: sequência de identificadores separados por
 * vírgula que representa todas as colunas do CSV.
 */
listaColunasDataset
    : IDENT (VIRGULA IDENT)*
    ;

// ============================================================
//  DIRETIVAS TARGET_VAR E FEATURES
// ============================================================

/**
 * diretiva: TARGET_VAR ou FEATURES, em qualquer ordem relativa.
 * A análise semântica valida que ambas estejam presentes.
 */
diretiva
    : diretívaTargetVar
    | diretívaFeatures
    ;

/**
 * diretívaTargetVar: define a coluna alvo (variável dependente).
 *
 * Exemplo:  TARGET_VAR comprou
 */
diretívaTargetVar
    : KW_TARGET_VAR IDENT
    ;

/**
 * diretívaFeatures: lista as colunas usadas como variáveis
 * independentes pelo modelo.
 *
 * Exemplo:  FEATURES idade, salario, regiao
 */
diretívaFeatures
    : KW_FEATURES IDENT (VIRGULA IDENT)*
    ;

// ============================================================
//  BLOCO MODEL
// ============================================================

/**
 * blocoModel: declara um modelo com nome, algoritmo e hiperparâmetros.
 * Múltiplos blocos MODEL são permitidos num mesmo programa.
 *
 * Exemplo:
 *   MODEL clf RandomForest
 *     n_estimators = 100
 *     max_depth    = 10
 *   END
 */
blocoModel
    : KW_MODEL IDENT algoritmo hiperparametro* KW_END
    ;

/**
 * algoritmo: nome do algoritmo de ML a ser instanciado.
 * Lista fechada — só os tokens abaixo são aceitos sintaticamente.
 *
 * Algoritmos de classificação : RandomForest, XGBoost,
 *                               LogisticRegression, SVM
 * Algoritmos de regressão     : LinearRegression
 *   (RandomForest e XGBoost também podem ser regressores — a
 *    distinção é feita pela análise semântica via TARGET_VAR ou
 *    pela escolha das métricas)
 */
algoritmo
    : KW_RANDOM_FOREST
    | KW_XGBOOST
    | KW_LINEAR_REGRESSION
    | KW_LOGISTIC_REGRESSION
    | KW_SVM
    ;

/**
 * hiperparametro: par chave = valor dentro de um bloco MODEL.
 *
 * Chaves possíveis (exemplos): n_estimators, max_depth,
 *   learning_rate, C, kernel, max_iter, random_state
 * Valores: inteiro, decimal (float) ou string (para hiperparâmetros
 *          do tipo kernel, solver, etc.)
 */
hiperparametro
    : IDENT IGUAL valorHiper
    ;

/**
 * valorHiper: valor de um hiperparâmetro — pode ser número inteiro,
 * número decimal ou cadeia de caracteres.
 */
valorHiper
    : NUM_INT
    | NUM_REAL
    | STRING
    ;

// ============================================================
//  DIRETIVA METRICS
// ============================================================

/**
 * diretivaMétrics: lista as métricas de avaliação a serem calculadas
 * e impressas após o treinamento.
 *
 * Métricas de classificação: accuracy, f1_score, precision, recall
 * Métricas de regressão    : RMSE, MAE, R2
 *
 * Exemplo:  METRICS accuracy, f1_score
 */
diretivaMétrics
    : KW_METRICS listaMetricas
    ;

/**
 * listaMetricas: sequência de métricas separadas por vírgula.
 * Cada métrica é um token específico (lista fechada).
 */
listaMetricas
    : metrica (VIRGULA metrica)*
    ;

/**
 * metrica: nome de uma métrica de avaliação suportada.
 */
metrica
    : KW_ACCURACY
    | KW_F1_SCORE
    | KW_PRECISION
    | KW_RECALL
    | KW_RMSE
    | KW_MAE
    | KW_R2
    ;

// ============================================================
//  DIRETIVA OUTPUT
// ============================================================

/**
 * diretívaOutput: caminho (string) onde o modelo treinado será salvo
 * via joblib.dump. A análise semântica pode validar a extensão do
 * arquivo (.pkl ou .joblib).
 *
 * Exemplo:  OUTPUT "modelos/meu_modelo.pkl"
 */
diretívaOutput
    : KW_OUTPUT STRING
    ;

// ============================================================
//  TOKENS — PALAVRAS-CHAVE ESTRUTURAIS
//  (declaradas antes de IDENT para garantir precedência)
// ============================================================

KW_DATASET            : 'DATASET'           ;
KW_COLUMNS            : 'COLUMNS'           ;
KW_END                : 'END'               ;
KW_TARGET_VAR         : 'TARGET_VAR'        ;
KW_FEATURES           : 'FEATURES'          ;
KW_MODEL              : 'MODEL'             ;
KW_METRICS            : 'METRICS'           ;
KW_OUTPUT             : 'OUTPUT'            ;

// ============================================================
//  TOKENS — ALGORITMOS (lista fechada)
// ============================================================

KW_RANDOM_FOREST      : 'RandomForest'      ;
KW_XGBOOST            : 'XGBoost'           ;
KW_LINEAR_REGRESSION  : 'LinearRegression'  ;
KW_LOGISTIC_REGRESSION: 'LogisticRegression';
KW_SVM                : 'SVM'               ;

// ============================================================
//  TOKENS — MÉTRICAS (lista fechada)
// ============================================================

KW_ACCURACY           : 'accuracy'          ;
KW_F1_SCORE           : 'f1_score'          ;
KW_PRECISION          : 'precision'         ;
KW_RECALL             : 'recall'            ;
KW_RMSE               : 'RMSE'              ;
KW_MAE                : 'MAE'              ;
KW_R2                 : 'R2'                ;

// ============================================================
//  TOKENS — LITERAIS E IDENTIFICADORES
// ============================================================

/**
 * NUM_REAL deve ser declarado ANTES de NUM_INT para que "3.14"
 * seja reconhecido como real e não como int + ponto + int.
 */
NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT  : [0-9]+              ;

/**
 * STRING: cadeia entre aspas duplas, sem quebra de linha.
 * Erros de cadeia não fechada são tratados por ERRO_STRING.
 */
STRING      : '"' ~["\r\n]* '"'  ;

/**
 * IDENT: identificador — começa com letra ou underscore,
 * seguido de letras, dígitos ou underscores.
 */
IDENT       : [a-zA-Z_][a-zA-Z0-9_]* ;

// ============================================================
//  TOKENS — SÍMBOLOS ESPECIAIS
// ============================================================

IGUAL       : '='  ;
VIRGULA     : ','  ;

// ============================================================
//  TOKENS DESCARTADOS
// ============================================================

/**
 * WS: espaços, tabs e quebras de linha são ignorados.
 */
WS          : [ \t\r\n]+        -> skip ;

/**
 * COMENTARIO_LINHA: comentários iniciados com // ou #
 * até o fim da linha são ignorados.
 */
COMENTARIO_LINHA
    : ('//' | '#') ~[\r\n]*     -> skip ;

// ============================================================
//  TOKENS DE ERRO LÉXICO
// ============================================================

/**
 * ERRO_STRING: cadeia literal aberta sem fechamento.
 * Reportada pelo CustomErrorListener como erro léxico.
 */
ERRO_STRING  : '"' ~["\r\n]*   ;

/**
 * ERRO_SIMBOLO: qualquer caractere não reconhecido.
 * Capturado pelo CustomErrorListener.
 */
ERRO_SIMBOLO : .                ;
