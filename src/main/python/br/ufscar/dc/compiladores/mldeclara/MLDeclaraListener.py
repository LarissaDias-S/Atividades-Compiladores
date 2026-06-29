# Generated from MLDeclara.g4 by ANTLR 4.11.1
from antlr4 import *
if __name__ is not None and "." in __name__:
    from .MLDeclaraParser import MLDeclaraParser
else:
    from MLDeclaraParser import MLDeclaraParser

# This class defines a complete listener for a parse tree produced by MLDeclaraParser.
class MLDeclaraListener(ParseTreeListener):

    # Enter a parse tree produced by MLDeclaraParser#programa.
    def enterPrograma(self, ctx:MLDeclaraParser.ProgramaContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#programa.
    def exitPrograma(self, ctx:MLDeclaraParser.ProgramaContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#blocoDataset.
    def enterBlocoDataset(self, ctx:MLDeclaraParser.BlocoDatasetContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#blocoDataset.
    def exitBlocoDataset(self, ctx:MLDeclaraParser.BlocoDatasetContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#listaColunasDataset.
    def enterListaColunasDataset(self, ctx:MLDeclaraParser.ListaColunasDatasetContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#listaColunasDataset.
    def exitListaColunasDataset(self, ctx:MLDeclaraParser.ListaColunasDatasetContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#diretiva.
    def enterDiretiva(self, ctx:MLDeclaraParser.DiretivaContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#diretiva.
    def exitDiretiva(self, ctx:MLDeclaraParser.DiretivaContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#diretívaTargetVar.
    def enterDiretívaTargetVar(self, ctx:MLDeclaraParser.DiretívaTargetVarContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#diretívaTargetVar.
    def exitDiretívaTargetVar(self, ctx:MLDeclaraParser.DiretívaTargetVarContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#diretívaFeatures.
    def enterDiretívaFeatures(self, ctx:MLDeclaraParser.DiretívaFeaturesContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#diretívaFeatures.
    def exitDiretívaFeatures(self, ctx:MLDeclaraParser.DiretívaFeaturesContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#blocoModel.
    def enterBlocoModel(self, ctx:MLDeclaraParser.BlocoModelContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#blocoModel.
    def exitBlocoModel(self, ctx:MLDeclaraParser.BlocoModelContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#algoritmo.
    def enterAlgoritmo(self, ctx:MLDeclaraParser.AlgoritmoContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#algoritmo.
    def exitAlgoritmo(self, ctx:MLDeclaraParser.AlgoritmoContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#hiperparametro.
    def enterHiperparametro(self, ctx:MLDeclaraParser.HiperparametroContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#hiperparametro.
    def exitHiperparametro(self, ctx:MLDeclaraParser.HiperparametroContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#valorHiper.
    def enterValorHiper(self, ctx:MLDeclaraParser.ValorHiperContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#valorHiper.
    def exitValorHiper(self, ctx:MLDeclaraParser.ValorHiperContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#diretivaMétrics.
    def enterDiretivaMétrics(self, ctx:MLDeclaraParser.DiretivaMétricsContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#diretivaMétrics.
    def exitDiretivaMétrics(self, ctx:MLDeclaraParser.DiretivaMétricsContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#listaMetricas.
    def enterListaMetricas(self, ctx:MLDeclaraParser.ListaMetricasContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#listaMetricas.
    def exitListaMetricas(self, ctx:MLDeclaraParser.ListaMetricasContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#metrica.
    def enterMetrica(self, ctx:MLDeclaraParser.MetricaContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#metrica.
    def exitMetrica(self, ctx:MLDeclaraParser.MetricaContext):
        pass


    # Enter a parse tree produced by MLDeclaraParser#diretívaOutput.
    def enterDiretívaOutput(self, ctx:MLDeclaraParser.DiretívaOutputContext):
        pass

    # Exit a parse tree produced by MLDeclaraParser#diretívaOutput.
    def exitDiretívaOutput(self, ctx:MLDeclaraParser.DiretívaOutputContext):
        pass



del MLDeclaraParser