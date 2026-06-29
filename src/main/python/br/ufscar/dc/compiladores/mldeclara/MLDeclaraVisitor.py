# Generated from MLDeclara.g4 by ANTLR 4.11.1
from antlr4 import *
if __name__ is not None and "." in __name__:
    from .MLDeclaraParser import MLDeclaraParser
else:
    from MLDeclaraParser import MLDeclaraParser

# This class defines a complete generic visitor for a parse tree produced by MLDeclaraParser.

class MLDeclaraVisitor(ParseTreeVisitor):

    # Visit a parse tree produced by MLDeclaraParser#programa.
    def visitPrograma(self, ctx:MLDeclaraParser.ProgramaContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#blocoDataset.
    def visitBlocoDataset(self, ctx:MLDeclaraParser.BlocoDatasetContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#listaColunasDataset.
    def visitListaColunasDataset(self, ctx:MLDeclaraParser.ListaColunasDatasetContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#diretiva.
    def visitDiretiva(self, ctx:MLDeclaraParser.DiretivaContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#diretívaTargetVar.
    def visitDiretívaTargetVar(self, ctx:MLDeclaraParser.DiretívaTargetVarContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#diretívaFeatures.
    def visitDiretívaFeatures(self, ctx:MLDeclaraParser.DiretívaFeaturesContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#blocoModel.
    def visitBlocoModel(self, ctx:MLDeclaraParser.BlocoModelContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#algoritmo.
    def visitAlgoritmo(self, ctx:MLDeclaraParser.AlgoritmoContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#hiperparametro.
    def visitHiperparametro(self, ctx:MLDeclaraParser.HiperparametroContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#valorHiper.
    def visitValorHiper(self, ctx:MLDeclaraParser.ValorHiperContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#diretivaMétrics.
    def visitDiretivaMétrics(self, ctx:MLDeclaraParser.DiretivaMétricsContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#listaMetricas.
    def visitListaMetricas(self, ctx:MLDeclaraParser.ListaMetricasContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#metrica.
    def visitMetrica(self, ctx:MLDeclaraParser.MetricaContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by MLDeclaraParser#diretívaOutput.
    def visitDiretívaOutput(self, ctx:MLDeclaraParser.DiretívaOutputContext):
        return self.visitChildren(ctx)



del MLDeclaraParser