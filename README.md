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

## Pré-requisitos
Para compilar e rodar este projeto, você precisará de:
* **Java JDK 17** ou superior.
* **Apache Maven 3.9.x** ou superior.

## Como Compilar
O projeto utiliza o Apache Maven para automação do build. Para gerar o compilador, execute o comando abaixo na raiz do projeto:

```bash
mvn clean package
```

Isso gerará a pasta `target/` contendo o arquivo executável `meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar` com todas as dependências incluídas.


## Como Executar
Para rodar o analisador léxico e processar um arquivo de código LA, utilize o seguinte comando no terminal:

```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```
Nota: Substitua <arquivo_entrada> pelo caminho do arquivo que você deseja analisar e <arquivo_saida> pelo nome do arquivo onde o resultado será salvo.

Exemplo de Uso:
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar teste.txt saida.txt

## Tratamento de Erros e saídas (T1 e T2)
O compilador foi validado com os casos de testes oficiais (62 casos no T2), garantindo o formato de saída exigido:

1. Erros Léxicos (T1): Reporta a linha e o tipo do erro (ex: Linha X: cadeia literal não fechada).

2. Erros Sintáticos (T2): Reporta o primeiro erro encontrado no formato Linha X: erro sintatico proximo a [lexema].

3. Encerramento: Todas as execuções, independentemente de erro, finalizam o arquivo de saída com a frase: 'Fim da compilacao'.