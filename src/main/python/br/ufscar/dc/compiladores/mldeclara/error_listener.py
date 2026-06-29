"""
error_listener.py — CustomErrorListener para o compilador MLDeclara.

Estende o BaseErrorListener do ANTLR4 para interceptar erros sintáticos
e formatá-los com linha, coluna e token causador do erro.

Analogamente ao CustomErrorListener.java do projeto LA (T1–T5), mas
adaptado para Python 3 e para a linguagem ML-Declara.

Responsabilidade: Pessoa 1 — ALS (Análise Léxica/Sintática).
"""

from antlr4.error.ErrorListener import ErrorListener


class MLDeclaraErrorListener(ErrorListener):
    """Listener de erros personalizado para a linguagem ML-Declara.

    Registra todos os erros sintáticos encontrados durante o parsing,
    com mensagens no formato:
        Erro sintático na linha <linha>, coluna <col>: próximo a '<token>'
    """

    def __init__(self):
        super().__init__()
        self._erros: list[str] = []

    # ------------------------------------------------------------------
    #  Interface ANTLR4
    # ------------------------------------------------------------------

    def syntaxError(self, recognizer, offendingSymbol, line, column, msg, e):
        """Chamado automaticamente pelo ANTLR4 a cada erro sintático.

        Args:
            recognizer     : o Lexer ou Parser que encontrou o erro.
            offendingSymbol: o token causador do problema (pode ser None).
            line           : linha do arquivo onde o erro ocorreu (1-based).
            column         : coluna onde o erro ocorreu (0-based).
            msg            : mensagem descritiva gerada pelo ANTLR.
            e              : a exceção de reconhecimento (pode ser None).
        """
        # Extrai o texto do token causador; normaliza EOF
        if offendingSymbol is not None:
            texto = offendingSymbol.text
            if texto in (None, "<EOF>"):
                texto = "EOF"
        else:
            texto = "EOF"

        mensagem = (
            f"Erro sintático na linha {line}, coluna {column + 1}: "
            f"próximo a '{texto}'"
        )
        self._erros.append(mensagem)

    # ------------------------------------------------------------------
    #  Helpers
    # ------------------------------------------------------------------

    @property
    def erros(self) -> list[str]:
        """Retorna a lista de erros registrados."""
        return self._erros

    @property
    def tem_erros(self) -> bool:
        """True se pelo menos um erro foi registrado."""
        return bool(self._erros)
