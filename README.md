# Projeto: Compilador da Linguagem Algorítmica (LA)

Este repositório contém o desenvolvimento do compilador para a linguagem LA, realizado para a disciplina de **Construção de Compiladores** (DC/UFSCar), ministrada pelo Prof. Daniel Lucrédio.

## Integrantes do Grupo
* **Bruna Matias de Lima** - RA: 820582
* **Julia Pedro Silva** - RA: 820869
* **Larissa Dias da Silva** - RA: 800204

## Fases do Projeto

### Trabalho 1: Analisador Léxico (Concluído ✅)
Implementação responsável pela leitura do programa-fonte e produção da lista de tokens identificados. Trata erros de símbolos não identificados, cadeias literais e comentários não fechados.

### Trabalho 2: Analisador Sintático (Concluído ✅)
Expansão do projeto para realizar a análise gramatical utilizando ANTLR4. O compilador agora detecta erros estruturais e interrompe a execução no primeiro erro encontrado, seguindo rigorosamente a especificação de saída.

### Trabalho 3: Analisador Semântico - Parte 1 (Concluído ✅)
Implementação inicial do analisador semântico. A partir da árvore sintática gerada, o compilador detecta erros de escopo (identificadores já declarados no mesmo nível), declaração (variáveis e tipos não declarados) e tipagem simples (atribuições incompatíveis). **Diferente da análise sintática, o analisador semântico não para no primeiro erro**, acumulando e reportando todas as inconsistências até o fim da leitura do arquivo.

### Trabalho 4: Analisador Semântico - Parte 2 (Concluído ✅)
Expansão completa do analisador semântico para detecção de erros avançados de tipo, escopo e contexto estruturado. O sistema foi homologado com 100% de sucesso nos testes oficiais da disciplina, cobrindo 5 novas categorias de erros:
1. **Conflito de Identificadores Expandido:** Verificação de redefinições de nomes abrangendo ponteiros, registros, funções e procedimentos (um mesmo identificador não pode ser reutilizado no mesmo escopo mesmo que para categorias distintas).
2. **Identificadores Não Declarados Avançados:** Resolução hierárquica e validação de nomes estendida a ponteiros, chamadas de sub-rotinas e acesso a propriedades internas de registros anônimos ou customizados.
3. **Incompatibilidade em Chamadas:** Validação rigorosa de correspondência entre os argumentos passados e os parâmetros formais (exigindo quantidade, ordem e compatibilidade estrita de tipos) em funções e procedimentos.
4. **Atribuição Avançada e Expressões Complexas:** Iniciação de inferência em expressões contendo operadores de endereço (`&`), desreferenciação de ponteiros (`^`) e correspondência nominal de registros (atribuições de registros exigem nomes de tipos idênticos). Tipos inválidos em expressões geram `tipo_indefinido` e inviabilizam a atribuição.
5. **Verificação de Contexto de Comando:** Validação estrutural para garantir que o comando `retorne` seja invocado exclusivamente dentro de blocos de funções (sendo proibido no algoritmo principal ou em procedimentos).

### Trabalho 5: Gerador de Código C (Concluído ✅)
Implementação do gerador de código que traduz programas LA válidos para C compilável pelo GCC. O pipeline completo do compilador agora é:

1. **Análise Léxica** — detecta o primeiro erro léxico.
2. **Análise Sintática** — detecta o primeiro erro sintático.
3. **Análise Semântica** — acumula todos os erros semânticos.
4. **Geração de Código C** — se não houver erros, produz código C executável.

Principais traduções implementadas:

| LA | C |
|---|---|
| `inteiro` | `int` |
| `real` | `float` |
| `literal` | `char nome[80]` |
| `^tipo` | `tipo *nome` |
| `registro ... fim_registro` | `struct { ... }` ou `typedef struct { ... } Tipo` |
| `funcao` / `procedimento` | função C declarada antes de `int main()` |
| `retorne` | `return` |
| `leia` / `escreva` | `scanf`/`gets` / `printf` |
| `se` / `enquanto` / `para` / `faca` | `if` / `while` / `for` / `do-while` |
| `=`, `<>`, `e`, `ou`, `nao` | `==`, `!=`, `&&`, `\|\|`, `!` |
| `^var`, `&var` | `*var`, `&var` |
| `constante` | `#define` |

## Pré-requisitos
Para compilar e rodar este projeto, você precisará de:
* **Java JDK 17** ou superior.
* **Apache Maven 3.9.x** ou superior.
* **GCC** (para compilar e executar o código C gerado — necessário apenas para validar os testes do T5).

## Como Compilar
O projeto utiliza o Apache Maven para automação do build. Para gerar o compilador executável, execute o comando abaixo na raiz do projeto (onde está localizado o arquivo `pom.xml`):

```bash
mvn clean package
```

Isso gerará a pasta `target/` contendo o arquivo compilado `meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar` com todas as dependências do ANTLR embutidas de forma autônoma.

## Como Executar
O compilador deve ser executado obrigatoriamente por linha de comando passando dois argumentos posicionais. O fluxo do programa suprime saídas no console do terminal e escreve o resultado diretamente no documento final.

```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

*Nota: Substitua `<arquivo_entrada>` pelo caminho do arquivo texto com o código em linguagem LA que deseja submeter à análise e `<arquivo_saida>` pelo caminho do documento de destino onde o compilador salvará o resultado estruturado.*

**Exemplo de Uso:**
```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar teste.txt saida.txt
```

## Tratamento de Erros e Saídas

O comportamento da saída depende do resultado da compilação:

| Situação | Conteúdo do arquivo de saída |
|---|---|
| Erro léxico, sintático ou semântico | Mensagens de erro + `Fim da compilacao` |
| Programa LA válido (T5) | Código C gerado (sem `Fim da compilacao`) |

Detalhes por fase:

1. **Erros Léxicos (T1):** Reporta a linha e a natureza de quebra gramatical básica (ex: `Linha X: cadeia literal nao fechada`).
2. **Erros Sintáticos (T2):** Aborta na primeira quebra sintática encontrada seguindo o padrão `Linha X: erro sintatico proximo a [lexema]`.
3. **Erros Semânticos (T3 e T4):** Realiza uma varredura completa coletando todas as violações semânticas (ex: `Linha X: comando retorne nao permitido nesse escopo`).
4. **Geração de Código (T5):** Se o programa não contém erros, o arquivo de saída contém exclusivamente o código C traduzido.

## Testes Automatizados do T5 (GCC)

Após compilar o projeto com `mvn clean package`, execute o script de testes que valida os 20 casos oficiais do T5:

**Windows:**
```bat
rodar_testes_t5.bat
```

**Linux / macOS:**
```bash
chmod +x rodar_testes_t5.sh
./rodar_testes_t5.sh
```

O script executa, para cada caso de teste:
1. Compila o arquivo `.alg` com o compilador LA → gera `.c`
2. Compila o `.c` gerado com **GCC**
3. Executa o binário (com entrada de stdin quando aplicável)
4. Compara a saída com o gabarito em `casos-de-teste/5.casos_teste_t5/4.saida/`

Os casos de teste estão organizados em:
* `1.entrada/` — programas LA de entrada
* `2.exemplos_codigo_C/` — exemplos de referência do código C esperado
* `3.entrada_execucao/` — dados de stdin para testes com `leia`
* `4.saida/` — saída esperada da execução do programa C