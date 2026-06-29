"""
ast_builder.py — Visitor que percorre a árvore de parsing do ANTLR4
e constrói a AST customizada (nós definidos em ast.py).

Este módulo é responsabilidade da Pessoa 1 (ALS).
A AST produzida aqui é consumida por:
  - Pessoa 2 (análise semântica) — via SemanticVisitor
  - Pessoa 3 (geração de código) — via CodeGenerator

Padrão de visita: herda de MLDeclaraVisitor (gerado pelo ANTLR4) e
sobrescreve apenas os métodos relevantes para a construção da AST.
"""

import sys
import os

# Garante que o diretório src está no path para imports relativos
sys.path.insert(0, os.path.dirname(__file__))

from MLDeclaraVisitor import MLDeclaraVisitor
from MLDeclaraParser import MLDeclaraParser
from mld_ast import (
    ProgramaNode,
    DatasetNode,
    ModeloNode,
    HiperparametroNode,
)


class ASTBuilder(MLDeclaraVisitor):
    """Percorre a parse tree e produz a AST simplificada do programa.

    Herda de MLDeclaraVisitor (gerado pelo ANTLR4).
    Cada método visitX corresponde a uma regra sintática X do arquivo .g4.

    Uso:
        builder = ASTBuilder()
        ast = builder.visit(parse_tree)   # retorna ProgramaNode
    """

    # ──────────────────────────────────────────────────────────
    #  Nó raiz
    # ──────────────────────────────────────────────────────────

    def visitPrograma(self, ctx: MLDeclaraParser.ProgramaContext) -> ProgramaNode:
        """Visita a regra 'programa' e monta o ProgramaNode.

        Coleta o bloco DATASET, as diretivas TARGET_VAR e FEATURES
        (que podem vir em qualquer ordem), os blocos MODEL, METRICS e OUTPUT.
        """
        # Bloco DATASET
        dataset_node = self.visitBlocoDataset(ctx.blocoDataset())

        # Diretivas TARGET_VAR e FEATURES (ordem livre)
        target_var = None
        features   = []

        for diretiva_ctx in ctx.diretiva():
            if diretiva_ctx.diretívaTargetVar():
                target_var = self.visitDiretívaTargetVar(diretiva_ctx.diretívaTargetVar())
            elif diretiva_ctx.diretívaFeatures():
                features = self.visitDiretívaFeatures(diretiva_ctx.diretívaFeatures())

        # Blocos MODEL (um ou mais)
        modelos = [self.visitBlocoModel(m) for m in ctx.blocoModel()]

        # METRICS
        metricas = self.visitDiretivaMétrics(ctx.diretivaMétrics())

        # OUTPUT
        output_path = self.visitDiretívaOutput(ctx.diretívaOutput())

        return ProgramaNode(
            dataset    = dataset_node,
            target_var = target_var or "",
            features   = features,
            modelos    = modelos,
            metricas   = metricas,
            output_path= output_path,
        )

    # ──────────────────────────────────────────────────────────
    #  Bloco DATASET
    # ──────────────────────────────────────────────────────────

    def visitBlocoDataset(self, ctx: MLDeclaraParser.BlocoDatasetContext) -> DatasetNode:
        """Constrói o DatasetNode a partir do bloco DATASET ... END."""
        # STRING vem com aspas — removemos as aspas ao guardar
        caminho = ctx.STRING().getText().strip('"')
        colunas = self.visitListaColunasDataset(ctx.listaColunasDataset())
        return DatasetNode(caminho=caminho, colunas=colunas)

    def visitListaColunasDataset(self, ctx: MLDeclaraParser.ListaColunasDatasetContext) -> list[str]:
        """Retorna lista de nomes de colunas (strings)."""
        return [ident.getText() for ident in ctx.IDENT()]

    # ──────────────────────────────────────────────────────────
    #  Diretivas
    # ──────────────────────────────────────────────────────────

    def visitDiretívaTargetVar(self, ctx: MLDeclaraParser.DiretívaTargetVarContext) -> str:
        """Retorna o nome da variável alvo."""
        return ctx.IDENT().getText()

    def visitDiretívaFeatures(self, ctx: MLDeclaraParser.DiretívaFeaturesContext) -> list[str]:
        """Retorna a lista de nomes de features."""
        return [ident.getText() for ident in ctx.IDENT()]

    # ──────────────────────────────────────────────────────────
    #  Bloco MODEL
    # ──────────────────────────────────────────────────────────

    def visitBlocoModel(self, ctx: MLDeclaraParser.BlocoModelContext) -> ModeloNode:
        """Constrói o ModeloNode a partir de um bloco MODEL ... END."""
        nome      = ctx.IDENT().getText()
        algoritmo = self.visitAlgoritmo(ctx.algoritmo())
        hipers    = [self.visitHiperparametro(h) for h in ctx.hiperparametro()]
        return ModeloNode(nome=nome, algoritmo=algoritmo, hiperparametros=hipers)

    def visitAlgoritmo(self, ctx: MLDeclaraParser.AlgoritmoContext) -> str:
        """Retorna o nome textual do algoritmo declarado."""
        return ctx.getText()

    def visitHiperparametro(self, ctx: MLDeclaraParser.HiperparametroContext) -> HiperparametroNode:
        """Constrói o HiperparametroNode infereindo o tipo do valor."""
        nome  = ctx.IDENT().getText()
        valor_ctx = ctx.valorHiper()
        valor, tipo = self._extrair_valor(valor_ctx)
        return HiperparametroNode(nome=nome, valor=valor, tipo=tipo)

    def visitValorHiper(self, ctx: MLDeclaraParser.ValorHiperContext):
        """Não chamado diretamente — use _extrair_valor."""
        return self._extrair_valor(ctx)

    # ──────────────────────────────────────────────────────────
    #  METRICS
    # ──────────────────────────────────────────────────────────

    def visitDiretivaMétrics(self, ctx: MLDeclaraParser.DiretivaMétricsContext) -> list[str]:
        """Retorna a lista de nomes de métricas."""
        return self.visitListaMetricas(ctx.listaMetricas())

    def visitListaMetricas(self, ctx: MLDeclaraParser.ListaMetricasContext) -> list[str]:
        """Retorna a lista de métricas como strings."""
        return [self.visitMetrica(m) for m in ctx.metrica()]

    def visitMetrica(self, ctx: MLDeclaraParser.MetricaContext) -> str:
        """Retorna o texto da métrica."""
        return ctx.getText()

    # ──────────────────────────────────────────────────────────
    #  OUTPUT
    # ──────────────────────────────────────────────────────────

    def visitDiretívaOutput(self, ctx: MLDeclaraParser.DiretívaOutputContext) -> str:
        """Retorna o caminho de saída sem aspas."""
        return ctx.STRING().getText().strip('"')

    # ──────────────────────────────────────────────────────────
    #  Helpers
    # ──────────────────────────────────────────────────────────

    @staticmethod
    def _extrair_valor(ctx: MLDeclaraParser.ValorHiperContext) -> tuple[str, str]:
        """Extrai o texto e o tipo ('int', 'float' ou 'str') de um valorHiper.

        Returns:
            (texto_do_valor, tipo_inferido)
        """
        if ctx.NUM_INT():
            return ctx.NUM_INT().getText(), "int"
        if ctx.NUM_REAL():
            return ctx.NUM_REAL().getText(), "float"
        # STRING — remove aspas
        return ctx.STRING().getText().strip('"'), "str"
