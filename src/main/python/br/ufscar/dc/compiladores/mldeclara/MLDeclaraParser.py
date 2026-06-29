# Generated from MLDeclara.g4 by ANTLR 4.11.1
# encoding: utf-8
from antlr4 import *
from io import StringIO
import sys
if sys.version_info[1] > 5:
	from typing import TextIO
else:
	from typing.io import TextIO

def serializedATN():
    return [
        4,1,30,109,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,6,7,
        6,2,7,7,7,2,8,7,8,2,9,7,9,2,10,7,10,2,11,7,11,2,12,7,12,2,13,7,13,
        1,0,1,0,4,0,31,8,0,11,0,12,0,32,1,0,4,0,36,8,0,11,0,12,0,37,1,0,
        1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,2,1,2,5,2,53,8,2,10,2,
        12,2,56,9,2,1,3,1,3,3,3,60,8,3,1,4,1,4,1,4,1,5,1,5,1,5,1,5,5,5,69,
        8,5,10,5,12,5,72,9,5,1,6,1,6,1,6,1,6,5,6,78,8,6,10,6,12,6,81,9,6,
        1,6,1,6,1,7,1,7,1,8,1,8,1,8,1,8,1,9,1,9,1,10,1,10,1,10,1,11,1,11,
        1,11,5,11,99,8,11,10,11,12,11,102,9,11,1,12,1,12,1,13,1,13,1,13,
        1,13,0,0,14,0,2,4,6,8,10,12,14,16,18,20,22,24,26,0,3,1,0,9,13,1,
        0,21,23,1,0,14,20,101,0,28,1,0,0,0,2,43,1,0,0,0,4,49,1,0,0,0,6,59,
        1,0,0,0,8,61,1,0,0,0,10,64,1,0,0,0,12,73,1,0,0,0,14,84,1,0,0,0,16,
        86,1,0,0,0,18,90,1,0,0,0,20,92,1,0,0,0,22,95,1,0,0,0,24,103,1,0,
        0,0,26,105,1,0,0,0,28,30,3,2,1,0,29,31,3,6,3,0,30,29,1,0,0,0,31,
        32,1,0,0,0,32,30,1,0,0,0,32,33,1,0,0,0,33,35,1,0,0,0,34,36,3,12,
        6,0,35,34,1,0,0,0,36,37,1,0,0,0,37,35,1,0,0,0,37,38,1,0,0,0,38,39,
        1,0,0,0,39,40,3,20,10,0,40,41,3,26,13,0,41,42,5,0,0,1,42,1,1,0,0,
        0,43,44,5,1,0,0,44,45,5,23,0,0,45,46,5,2,0,0,46,47,3,4,2,0,47,48,
        5,3,0,0,48,3,1,0,0,0,49,54,5,24,0,0,50,51,5,26,0,0,51,53,5,24,0,
        0,52,50,1,0,0,0,53,56,1,0,0,0,54,52,1,0,0,0,54,55,1,0,0,0,55,5,1,
        0,0,0,56,54,1,0,0,0,57,60,3,8,4,0,58,60,3,10,5,0,59,57,1,0,0,0,59,
        58,1,0,0,0,60,7,1,0,0,0,61,62,5,4,0,0,62,63,5,24,0,0,63,9,1,0,0,
        0,64,65,5,5,0,0,65,70,5,24,0,0,66,67,5,26,0,0,67,69,5,24,0,0,68,
        66,1,0,0,0,69,72,1,0,0,0,70,68,1,0,0,0,70,71,1,0,0,0,71,11,1,0,0,
        0,72,70,1,0,0,0,73,74,5,6,0,0,74,75,5,24,0,0,75,79,3,14,7,0,76,78,
        3,16,8,0,77,76,1,0,0,0,78,81,1,0,0,0,79,77,1,0,0,0,79,80,1,0,0,0,
        80,82,1,0,0,0,81,79,1,0,0,0,82,83,5,3,0,0,83,13,1,0,0,0,84,85,7,
        0,0,0,85,15,1,0,0,0,86,87,5,24,0,0,87,88,5,25,0,0,88,89,3,18,9,0,
        89,17,1,0,0,0,90,91,7,1,0,0,91,19,1,0,0,0,92,93,5,7,0,0,93,94,3,
        22,11,0,94,21,1,0,0,0,95,100,3,24,12,0,96,97,5,26,0,0,97,99,3,24,
        12,0,98,96,1,0,0,0,99,102,1,0,0,0,100,98,1,0,0,0,100,101,1,0,0,0,
        101,23,1,0,0,0,102,100,1,0,0,0,103,104,7,2,0,0,104,25,1,0,0,0,105,
        106,5,8,0,0,106,107,5,23,0,0,107,27,1,0,0,0,7,32,37,54,59,70,79,
        100
    ]

class MLDeclaraParser ( Parser ):

    grammarFileName = "MLDeclara.g4"

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    sharedContextCache = PredictionContextCache()

    literalNames = [ "<INVALID>", "'DATASET'", "'COLUMNS'", "'END'", "'TARGET_VAR'", 
                     "'FEATURES'", "'MODEL'", "'METRICS'", "'OUTPUT'", "'RandomForest'", 
                     "'XGBoost'", "'LinearRegression'", "'LogisticRegression'", 
                     "'SVM'", "'accuracy'", "'f1_score'", "'precision'", 
                     "'recall'", "'RMSE'", "'MAE'", "'R2'", "<INVALID>", 
                     "<INVALID>", "<INVALID>", "<INVALID>", "'='", "','" ]

    symbolicNames = [ "<INVALID>", "KW_DATASET", "KW_COLUMNS", "KW_END", 
                      "KW_TARGET_VAR", "KW_FEATURES", "KW_MODEL", "KW_METRICS", 
                      "KW_OUTPUT", "KW_RANDOM_FOREST", "KW_XGBOOST", "KW_LINEAR_REGRESSION", 
                      "KW_LOGISTIC_REGRESSION", "KW_SVM", "KW_ACCURACY", 
                      "KW_F1_SCORE", "KW_PRECISION", "KW_RECALL", "KW_RMSE", 
                      "KW_MAE", "KW_R2", "NUM_REAL", "NUM_INT", "STRING", 
                      "IDENT", "IGUAL", "VIRGULA", "WS", "COMENTARIO_LINHA", 
                      "ERRO_STRING", "ERRO_SIMBOLO" ]

    RULE_programa = 0
    RULE_blocoDataset = 1
    RULE_listaColunasDataset = 2
    RULE_diretiva = 3
    RULE_diretívaTargetVar = 4
    RULE_diretívaFeatures = 5
    RULE_blocoModel = 6
    RULE_algoritmo = 7
    RULE_hiperparametro = 8
    RULE_valorHiper = 9
    RULE_diretivaMétrics = 10
    RULE_listaMetricas = 11
    RULE_metrica = 12
    RULE_diretívaOutput = 13

    ruleNames =  [ "programa", "blocoDataset", "listaColunasDataset", "diretiva", 
                   "diretívaTargetVar", "diretívaFeatures", "blocoModel", 
                   "algoritmo", "hiperparametro", "valorHiper", "diretivaMétrics", 
                   "listaMetricas", "metrica", "diretívaOutput" ]

    EOF = Token.EOF
    KW_DATASET=1
    KW_COLUMNS=2
    KW_END=3
    KW_TARGET_VAR=4
    KW_FEATURES=5
    KW_MODEL=6
    KW_METRICS=7
    KW_OUTPUT=8
    KW_RANDOM_FOREST=9
    KW_XGBOOST=10
    KW_LINEAR_REGRESSION=11
    KW_LOGISTIC_REGRESSION=12
    KW_SVM=13
    KW_ACCURACY=14
    KW_F1_SCORE=15
    KW_PRECISION=16
    KW_RECALL=17
    KW_RMSE=18
    KW_MAE=19
    KW_R2=20
    NUM_REAL=21
    NUM_INT=22
    STRING=23
    IDENT=24
    IGUAL=25
    VIRGULA=26
    WS=27
    COMENTARIO_LINHA=28
    ERRO_STRING=29
    ERRO_SIMBOLO=30

    def __init__(self, input:TokenStream, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.11.1")
        self._interp = ParserATNSimulator(self, self.atn, self.decisionsToDFA, self.sharedContextCache)
        self._predicates = None




    class ProgramaContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def blocoDataset(self):
            return self.getTypedRuleContext(MLDeclaraParser.BlocoDatasetContext,0)


        def diretivaMétrics(self):
            return self.getTypedRuleContext(MLDeclaraParser.DiretivaMétricsContext,0)


        def diretívaOutput(self):
            return self.getTypedRuleContext(MLDeclaraParser.DiretívaOutputContext,0)


        def EOF(self):
            return self.getToken(MLDeclaraParser.EOF, 0)

        def diretiva(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(MLDeclaraParser.DiretivaContext)
            else:
                return self.getTypedRuleContext(MLDeclaraParser.DiretivaContext,i)


        def blocoModel(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(MLDeclaraParser.BlocoModelContext)
            else:
                return self.getTypedRuleContext(MLDeclaraParser.BlocoModelContext,i)


        def getRuleIndex(self):
            return MLDeclaraParser.RULE_programa

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterPrograma" ):
                listener.enterPrograma(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitPrograma" ):
                listener.exitPrograma(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitPrograma" ):
                return visitor.visitPrograma(self)
            else:
                return visitor.visitChildren(self)




    def programa(self):

        localctx = MLDeclaraParser.ProgramaContext(self, self._ctx, self.state)
        self.enterRule(localctx, 0, self.RULE_programa)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 28
            self.blocoDataset()
            self.state = 30 
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while True:
                self.state = 29
                self.diretiva()
                self.state = 32 
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                if not (_la==4 or _la==5):
                    break

            self.state = 35 
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while True:
                self.state = 34
                self.blocoModel()
                self.state = 37 
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                if not (_la==6):
                    break

            self.state = 39
            self.diretivaMétrics()
            self.state = 40
            self.diretívaOutput()
            self.state = 41
            self.match(MLDeclaraParser.EOF)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class BlocoDatasetContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_DATASET(self):
            return self.getToken(MLDeclaraParser.KW_DATASET, 0)

        def STRING(self):
            return self.getToken(MLDeclaraParser.STRING, 0)

        def KW_COLUMNS(self):
            return self.getToken(MLDeclaraParser.KW_COLUMNS, 0)

        def listaColunasDataset(self):
            return self.getTypedRuleContext(MLDeclaraParser.ListaColunasDatasetContext,0)


        def KW_END(self):
            return self.getToken(MLDeclaraParser.KW_END, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_blocoDataset

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterBlocoDataset" ):
                listener.enterBlocoDataset(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitBlocoDataset" ):
                listener.exitBlocoDataset(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitBlocoDataset" ):
                return visitor.visitBlocoDataset(self)
            else:
                return visitor.visitChildren(self)




    def blocoDataset(self):

        localctx = MLDeclaraParser.BlocoDatasetContext(self, self._ctx, self.state)
        self.enterRule(localctx, 2, self.RULE_blocoDataset)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 43
            self.match(MLDeclaraParser.KW_DATASET)
            self.state = 44
            self.match(MLDeclaraParser.STRING)
            self.state = 45
            self.match(MLDeclaraParser.KW_COLUMNS)
            self.state = 46
            self.listaColunasDataset()
            self.state = 47
            self.match(MLDeclaraParser.KW_END)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class ListaColunasDatasetContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def IDENT(self, i:int=None):
            if i is None:
                return self.getTokens(MLDeclaraParser.IDENT)
            else:
                return self.getToken(MLDeclaraParser.IDENT, i)

        def VIRGULA(self, i:int=None):
            if i is None:
                return self.getTokens(MLDeclaraParser.VIRGULA)
            else:
                return self.getToken(MLDeclaraParser.VIRGULA, i)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_listaColunasDataset

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterListaColunasDataset" ):
                listener.enterListaColunasDataset(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitListaColunasDataset" ):
                listener.exitListaColunasDataset(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitListaColunasDataset" ):
                return visitor.visitListaColunasDataset(self)
            else:
                return visitor.visitChildren(self)




    def listaColunasDataset(self):

        localctx = MLDeclaraParser.ListaColunasDatasetContext(self, self._ctx, self.state)
        self.enterRule(localctx, 4, self.RULE_listaColunasDataset)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 49
            self.match(MLDeclaraParser.IDENT)
            self.state = 54
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==26:
                self.state = 50
                self.match(MLDeclaraParser.VIRGULA)
                self.state = 51
                self.match(MLDeclaraParser.IDENT)
                self.state = 56
                self._errHandler.sync(self)
                _la = self._input.LA(1)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class DiretivaContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def diretívaTargetVar(self):
            return self.getTypedRuleContext(MLDeclaraParser.DiretívaTargetVarContext,0)


        def diretívaFeatures(self):
            return self.getTypedRuleContext(MLDeclaraParser.DiretívaFeaturesContext,0)


        def getRuleIndex(self):
            return MLDeclaraParser.RULE_diretiva

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterDiretiva" ):
                listener.enterDiretiva(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitDiretiva" ):
                listener.exitDiretiva(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitDiretiva" ):
                return visitor.visitDiretiva(self)
            else:
                return visitor.visitChildren(self)




    def diretiva(self):

        localctx = MLDeclaraParser.DiretivaContext(self, self._ctx, self.state)
        self.enterRule(localctx, 6, self.RULE_diretiva)
        try:
            self.state = 59
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [4]:
                self.enterOuterAlt(localctx, 1)
                self.state = 57
                self.diretívaTargetVar()
                pass
            elif token in [5]:
                self.enterOuterAlt(localctx, 2)
                self.state = 58
                self.diretívaFeatures()
                pass
            else:
                raise NoViableAltException(self)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class DiretívaTargetVarContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_TARGET_VAR(self):
            return self.getToken(MLDeclaraParser.KW_TARGET_VAR, 0)

        def IDENT(self):
            return self.getToken(MLDeclaraParser.IDENT, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_diretívaTargetVar

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterDiretívaTargetVar" ):
                listener.enterDiretívaTargetVar(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitDiretívaTargetVar" ):
                listener.exitDiretívaTargetVar(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitDiretívaTargetVar" ):
                return visitor.visitDiretívaTargetVar(self)
            else:
                return visitor.visitChildren(self)




    def diretívaTargetVar(self):

        localctx = MLDeclaraParser.DiretívaTargetVarContext(self, self._ctx, self.state)
        self.enterRule(localctx, 8, self.RULE_diretívaTargetVar)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 61
            self.match(MLDeclaraParser.KW_TARGET_VAR)
            self.state = 62
            self.match(MLDeclaraParser.IDENT)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class DiretívaFeaturesContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_FEATURES(self):
            return self.getToken(MLDeclaraParser.KW_FEATURES, 0)

        def IDENT(self, i:int=None):
            if i is None:
                return self.getTokens(MLDeclaraParser.IDENT)
            else:
                return self.getToken(MLDeclaraParser.IDENT, i)

        def VIRGULA(self, i:int=None):
            if i is None:
                return self.getTokens(MLDeclaraParser.VIRGULA)
            else:
                return self.getToken(MLDeclaraParser.VIRGULA, i)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_diretívaFeatures

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterDiretívaFeatures" ):
                listener.enterDiretívaFeatures(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitDiretívaFeatures" ):
                listener.exitDiretívaFeatures(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitDiretívaFeatures" ):
                return visitor.visitDiretívaFeatures(self)
            else:
                return visitor.visitChildren(self)




    def diretívaFeatures(self):

        localctx = MLDeclaraParser.DiretívaFeaturesContext(self, self._ctx, self.state)
        self.enterRule(localctx, 10, self.RULE_diretívaFeatures)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 64
            self.match(MLDeclaraParser.KW_FEATURES)
            self.state = 65
            self.match(MLDeclaraParser.IDENT)
            self.state = 70
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==26:
                self.state = 66
                self.match(MLDeclaraParser.VIRGULA)
                self.state = 67
                self.match(MLDeclaraParser.IDENT)
                self.state = 72
                self._errHandler.sync(self)
                _la = self._input.LA(1)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class BlocoModelContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_MODEL(self):
            return self.getToken(MLDeclaraParser.KW_MODEL, 0)

        def IDENT(self):
            return self.getToken(MLDeclaraParser.IDENT, 0)

        def algoritmo(self):
            return self.getTypedRuleContext(MLDeclaraParser.AlgoritmoContext,0)


        def KW_END(self):
            return self.getToken(MLDeclaraParser.KW_END, 0)

        def hiperparametro(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(MLDeclaraParser.HiperparametroContext)
            else:
                return self.getTypedRuleContext(MLDeclaraParser.HiperparametroContext,i)


        def getRuleIndex(self):
            return MLDeclaraParser.RULE_blocoModel

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterBlocoModel" ):
                listener.enterBlocoModel(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitBlocoModel" ):
                listener.exitBlocoModel(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitBlocoModel" ):
                return visitor.visitBlocoModel(self)
            else:
                return visitor.visitChildren(self)




    def blocoModel(self):

        localctx = MLDeclaraParser.BlocoModelContext(self, self._ctx, self.state)
        self.enterRule(localctx, 12, self.RULE_blocoModel)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 73
            self.match(MLDeclaraParser.KW_MODEL)
            self.state = 74
            self.match(MLDeclaraParser.IDENT)
            self.state = 75
            self.algoritmo()
            self.state = 79
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==24:
                self.state = 76
                self.hiperparametro()
                self.state = 81
                self._errHandler.sync(self)
                _la = self._input.LA(1)

            self.state = 82
            self.match(MLDeclaraParser.KW_END)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class AlgoritmoContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_RANDOM_FOREST(self):
            return self.getToken(MLDeclaraParser.KW_RANDOM_FOREST, 0)

        def KW_XGBOOST(self):
            return self.getToken(MLDeclaraParser.KW_XGBOOST, 0)

        def KW_LINEAR_REGRESSION(self):
            return self.getToken(MLDeclaraParser.KW_LINEAR_REGRESSION, 0)

        def KW_LOGISTIC_REGRESSION(self):
            return self.getToken(MLDeclaraParser.KW_LOGISTIC_REGRESSION, 0)

        def KW_SVM(self):
            return self.getToken(MLDeclaraParser.KW_SVM, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_algoritmo

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterAlgoritmo" ):
                listener.enterAlgoritmo(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitAlgoritmo" ):
                listener.exitAlgoritmo(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitAlgoritmo" ):
                return visitor.visitAlgoritmo(self)
            else:
                return visitor.visitChildren(self)




    def algoritmo(self):

        localctx = MLDeclaraParser.AlgoritmoContext(self, self._ctx, self.state)
        self.enterRule(localctx, 14, self.RULE_algoritmo)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 84
            _la = self._input.LA(1)
            if not(((_la) & ~0x3f) == 0 and ((1 << _la) & 15872) != 0):
                self._errHandler.recoverInline(self)
            else:
                self._errHandler.reportMatch(self)
                self.consume()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class HiperparametroContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def IDENT(self):
            return self.getToken(MLDeclaraParser.IDENT, 0)

        def IGUAL(self):
            return self.getToken(MLDeclaraParser.IGUAL, 0)

        def valorHiper(self):
            return self.getTypedRuleContext(MLDeclaraParser.ValorHiperContext,0)


        def getRuleIndex(self):
            return MLDeclaraParser.RULE_hiperparametro

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterHiperparametro" ):
                listener.enterHiperparametro(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitHiperparametro" ):
                listener.exitHiperparametro(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitHiperparametro" ):
                return visitor.visitHiperparametro(self)
            else:
                return visitor.visitChildren(self)




    def hiperparametro(self):

        localctx = MLDeclaraParser.HiperparametroContext(self, self._ctx, self.state)
        self.enterRule(localctx, 16, self.RULE_hiperparametro)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 86
            self.match(MLDeclaraParser.IDENT)
            self.state = 87
            self.match(MLDeclaraParser.IGUAL)
            self.state = 88
            self.valorHiper()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class ValorHiperContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def NUM_INT(self):
            return self.getToken(MLDeclaraParser.NUM_INT, 0)

        def NUM_REAL(self):
            return self.getToken(MLDeclaraParser.NUM_REAL, 0)

        def STRING(self):
            return self.getToken(MLDeclaraParser.STRING, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_valorHiper

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterValorHiper" ):
                listener.enterValorHiper(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitValorHiper" ):
                listener.exitValorHiper(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitValorHiper" ):
                return visitor.visitValorHiper(self)
            else:
                return visitor.visitChildren(self)




    def valorHiper(self):

        localctx = MLDeclaraParser.ValorHiperContext(self, self._ctx, self.state)
        self.enterRule(localctx, 18, self.RULE_valorHiper)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 90
            _la = self._input.LA(1)
            if not(((_la) & ~0x3f) == 0 and ((1 << _la) & 14680064) != 0):
                self._errHandler.recoverInline(self)
            else:
                self._errHandler.reportMatch(self)
                self.consume()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class DiretivaMétricsContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_METRICS(self):
            return self.getToken(MLDeclaraParser.KW_METRICS, 0)

        def listaMetricas(self):
            return self.getTypedRuleContext(MLDeclaraParser.ListaMetricasContext,0)


        def getRuleIndex(self):
            return MLDeclaraParser.RULE_diretivaMétrics

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterDiretivaMétrics" ):
                listener.enterDiretivaMétrics(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitDiretivaMétrics" ):
                listener.exitDiretivaMétrics(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitDiretivaMétrics" ):
                return visitor.visitDiretivaMétrics(self)
            else:
                return visitor.visitChildren(self)




    def diretivaMétrics(self):

        localctx = MLDeclaraParser.DiretivaMétricsContext(self, self._ctx, self.state)
        self.enterRule(localctx, 20, self.RULE_diretivaMétrics)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 92
            self.match(MLDeclaraParser.KW_METRICS)
            self.state = 93
            self.listaMetricas()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class ListaMetricasContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def metrica(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(MLDeclaraParser.MetricaContext)
            else:
                return self.getTypedRuleContext(MLDeclaraParser.MetricaContext,i)


        def VIRGULA(self, i:int=None):
            if i is None:
                return self.getTokens(MLDeclaraParser.VIRGULA)
            else:
                return self.getToken(MLDeclaraParser.VIRGULA, i)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_listaMetricas

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterListaMetricas" ):
                listener.enterListaMetricas(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitListaMetricas" ):
                listener.exitListaMetricas(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitListaMetricas" ):
                return visitor.visitListaMetricas(self)
            else:
                return visitor.visitChildren(self)




    def listaMetricas(self):

        localctx = MLDeclaraParser.ListaMetricasContext(self, self._ctx, self.state)
        self.enterRule(localctx, 22, self.RULE_listaMetricas)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 95
            self.metrica()
            self.state = 100
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==26:
                self.state = 96
                self.match(MLDeclaraParser.VIRGULA)
                self.state = 97
                self.metrica()
                self.state = 102
                self._errHandler.sync(self)
                _la = self._input.LA(1)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class MetricaContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_ACCURACY(self):
            return self.getToken(MLDeclaraParser.KW_ACCURACY, 0)

        def KW_F1_SCORE(self):
            return self.getToken(MLDeclaraParser.KW_F1_SCORE, 0)

        def KW_PRECISION(self):
            return self.getToken(MLDeclaraParser.KW_PRECISION, 0)

        def KW_RECALL(self):
            return self.getToken(MLDeclaraParser.KW_RECALL, 0)

        def KW_RMSE(self):
            return self.getToken(MLDeclaraParser.KW_RMSE, 0)

        def KW_MAE(self):
            return self.getToken(MLDeclaraParser.KW_MAE, 0)

        def KW_R2(self):
            return self.getToken(MLDeclaraParser.KW_R2, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_metrica

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterMetrica" ):
                listener.enterMetrica(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitMetrica" ):
                listener.exitMetrica(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitMetrica" ):
                return visitor.visitMetrica(self)
            else:
                return visitor.visitChildren(self)




    def metrica(self):

        localctx = MLDeclaraParser.MetricaContext(self, self._ctx, self.state)
        self.enterRule(localctx, 24, self.RULE_metrica)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 103
            _la = self._input.LA(1)
            if not(((_la) & ~0x3f) == 0 and ((1 << _la) & 2080768) != 0):
                self._errHandler.recoverInline(self)
            else:
                self._errHandler.reportMatch(self)
                self.consume()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx


    class DiretívaOutputContext(ParserRuleContext):
        __slots__ = 'parser'

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def KW_OUTPUT(self):
            return self.getToken(MLDeclaraParser.KW_OUTPUT, 0)

        def STRING(self):
            return self.getToken(MLDeclaraParser.STRING, 0)

        def getRuleIndex(self):
            return MLDeclaraParser.RULE_diretívaOutput

        def enterRule(self, listener:ParseTreeListener):
            if hasattr( listener, "enterDiretívaOutput" ):
                listener.enterDiretívaOutput(self)

        def exitRule(self, listener:ParseTreeListener):
            if hasattr( listener, "exitDiretívaOutput" ):
                listener.exitDiretívaOutput(self)

        def accept(self, visitor:ParseTreeVisitor):
            if hasattr( visitor, "visitDiretívaOutput" ):
                return visitor.visitDiretívaOutput(self)
            else:
                return visitor.visitChildren(self)




    def diretívaOutput(self):

        localctx = MLDeclaraParser.DiretívaOutputContext(self, self._ctx, self.state)
        self.enterRule(localctx, 26, self.RULE_diretívaOutput)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 105
            self.match(MLDeclaraParser.KW_OUTPUT)
            self.state = 106
            self.match(MLDeclaraParser.STRING)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx





