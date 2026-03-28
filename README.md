# Trabalho 1: Analisador Léxico (Linguagem Algorítmica - LA)

Este repositório contém a implementação do Trabalho 1 da disciplina de **Construção de Compiladores** (DC/UFSCar), ministrada pelo Prof. Daniel Lucrédio.

## Integrantes do Grupo
* **Bruna Matias de Lima** - RA: 820582
* **Julia Pedro Silva** - RA: 820869
* **Larissa Dias da Silva** - RA: 800204

## Pré-requisitos
Para compilar e rodar este projeto, você precisará de:
* **Java JDK 17** ou superior.
* **Apache Maven 3.9.x** ou superior.

## Como Compilar
O projeto utiliza o Apache Maven para automação do build. Para gerar o compilador, execute o comando abaixo na raiz do projeto:

```bash
mvn clean package
```

Isso gerará a pasta `target/` contendo o arquivo executável `.jar` com todas as dependências incluídas.


## Como Executar
Para rodar o analisador léxico e processar um arquivo de código LA, utilize o seguinte comando no terminal:

```bash
java -jar target/meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

## Casos de Teste Validados
O grupo validou o analisador utilizando a suíte de testes oficiais da disciplina, garantindo o tratamento correto de:

Tokens Válidos: Reconhecimento de palavras-chave, identificadores, números (inteiros e reais) e operadores.

Comentários não fechados: Identificados com a mensagem Linha X: comentario nao fechado.

Cadeias não fechadas: Identificadas com a mensagem Linha X: cadeia literal nao fechada.

Símbolos não identificados: Identificados com a mensagem Linha X: <simbolo> - simbolo nao identificado.




