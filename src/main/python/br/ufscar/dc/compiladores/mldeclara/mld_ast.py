"""
ast.py — Definição dos nós da AST (Abstract Syntax Tree) do compilador MLDeclara.

A AST é uma estrutura de dados simplificada construída a partir da árvore
"crua" gerada pelo ANTLR4. Ela é a interface entre os três módulos do compilador:
  - Pessoa 1 (ALS): constrói a AST a partir da árvore de parsing.
  - Pessoa 2 (AS) : percorre a AST para validações semânticas.
  - Pessoa 3 (GCI): percorre a AST para geração do código Python.

Cada dataclass corresponde a um conceito da linguagem ML-Declara.
"""

from dataclasses import dataclass, field
from typing import List, Optional, Union


# ──────────────────────────────────────────────────────────────
#  Nó raiz
# ──────────────────────────────────────────────────────────────

@dataclass
class ProgramaNode:
    """Nó raiz da AST. Contém todos os outros nós do programa."""
    dataset: "DatasetNode"
    target_var: str                  # nome da coluna alvo
    features: List[str]              # lista de colunas de entrada
    modelos: List["ModeloNode"]      # um ou mais blocos MODEL
    metricas: List[str]              # métricas declaradas
    output_path: str                 # caminho do arquivo de saída


# ──────────────────────────────────────────────────────────────
#  Bloco DATASET
# ──────────────────────────────────────────────────────────────

@dataclass
class DatasetNode:
    """Representa o bloco DATASET ... END.

    Atributos:
        caminho  : caminho do arquivo CSV (sem aspas).
        colunas  : lista de nomes de colunas declaradas.
    """
    caminho: str
    colunas: List[str]


# ──────────────────────────────────────────────────────────────
#  Bloco MODEL
# ──────────────────────────────────────────────────────────────

@dataclass
class HiperparametroNode:
    """Representa um par chave = valor dentro de um bloco MODEL.

    Atributos:
        nome  : nome do hiperparâmetro (ex.: "max_depth").
        valor : valor literal como string (ex.: "10", "0.1", "gini").
        tipo  : "int", "float" ou "str" — inferido durante a construção da AST.
    """
    nome: str
    valor: str
    tipo: str       # "int" | "float" | "str"


@dataclass
class ModeloNode:
    """Representa um bloco MODEL <nome> <algoritmo> ... END.

    Atributos:
        nome           : identificador do modelo (ex.: "clf").
        algoritmo      : nome do algoritmo (ex.: "RandomForest").
        hiperparametros: lista de hiperparâmetros declarados.
    """
    nome: str
    algoritmo: str
    hiperparametros: List[HiperparametroNode] = field(default_factory=list)
