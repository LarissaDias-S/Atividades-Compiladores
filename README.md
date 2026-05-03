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

### Trabalho 3: Analisador Semântico (Concluído ✅)
Implementação do analisador semântico. A partir da árvore sintática gerada, o compilador detecta erros de escopo (identificadores já declarados no mesmo nível), declaração (variáveis e tipos não declarados) e tipagem (atribuições incompatíveis). **Diferente da análise sintática, o analisador semântico não para no primeiro erro**, acumulando e reportando todas as inconsistências até o fim da leitura do arquivo.

## Pré-requisitos
Para compilar e rodar este projeto, você precisará de:
* **Java JDK 17** ou superior.
* **Apache Maven 3.9.x** ou superior.

## Como Compilar
O projeto utiliza o Apache Maven para automação do build. Para gerar o compilador, execute o comando abaixo na raiz do projeto (onde está localizado o arquivo pom.xml):
```bash
mvn clean package
```

Isso gerará a pasta target/ contendo o arquivo executável meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar com todas as dependências do ANTLR incluídas.

## Como Executar
O compilador deve ser executado via terminal, sendo **obrigatória** a passagem de dois argumentos (arquivo de entrada e arquivo de saída). As mensagens não são impressas no terminal.

```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

*Nota: Substitua `<arquivo_entrada>` pelo caminho do arquivo de código LA que você deseja analisar e `<arquivo_saida>` pelo caminho do arquivo texto onde o resultado e os erros serão gravados.*

**Exemplo de Uso:**
```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar teste.txt saida.txt
```

## Tratamento de Erros e Saídas
O compilador foi validado com os casos de testes oficiais da disciplina (ex: os 9 casos do T3 e 62 casos do T2), garantindo o formato exato de saída exigido:

1. **Erros Léxicos (T1):** Reporta a linha e o tipo do erro (ex: Linha X: cadeia literal nao fechada).
2. **Erros Sintáticos (T2):** Reporta o primeiro erro encontrado no formato Linha X: erro sintatico proximo a [lexema].
3. **Erros Semânticos (T3):** Reporta todos os erros semânticos encontrados ao longo do arquivo, como tipos incompatíveis ou escopos inválidos (ex: Linha X: identificador [nome] ja declarado anteriormente).
4. **Encerramento:** Todas as execuções, independentemente de erro ou sucesso, finalizam o arquivo de saída com a frase: Fim da compilacao.