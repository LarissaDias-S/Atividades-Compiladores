"""
main.py — Ponto de entrada do compilador MLDeclara.

Pipeline completo:
  1. Análise Léxica   — detecta erros de tokens (cadeia não fechada,
                        símbolo inválido).
  2. Análise Sintática — detecta erros estruturais usando o parser
                         gerado pelo ANTLR4; gera a parse tree.
  3. Construção da AST — o ASTBuilder percorre a parse tree e produz
                         a AST customizada (nós de ast.py).
  4. [Análise Semântica — módulo da Pessoa 2, invocado aqui quando pronto]
  5. [Geração de Código — módulo da Pessoa 3, invocado aqui quando pronto]

Uso:
  python main.py <arquivo.mld>

Disciplina: Construção de Compiladores — Prof. Daniel Lucrédio | UFSCar
"""

import sys
import os
import io

# Garante que o diretório do módulo esteja no path (funciona independente de onde main.py é chamado)
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from antlr4 import CommonTokenStream, InputStream
from MLDeclaraLexer import MLDeclaraLexer
from MLDeclaraParser import MLDeclaraParser
from error_listener import MLDeclaraErrorListener
from ast_builder import ASTBuilder


def compilar(codigo_fonte: str, nome_arquivo: str = "<stdin>"):
    """Executa o pipeline de compilação do MLDeclara.

    Args:
        codigo_fonte : conteúdo do arquivo .mld como string.
        nome_arquivo : nome do arquivo (usado nas mensagens de erro).

    Returns:
        ProgramaNode se a compilação foi bem-sucedida, None caso contrário.
    """

    # ── ETAPA 1: Análise Léxica ─────────────────────────────────────
    # Redireciona stdout temporariamente para suprimir o print de divergência
    # de versão do ANTLR (aviso inofensivo: jar 4.11.1 vs runtime 4.13.2).
    input_stream = InputStream(codigo_fonte)
    _stdout_orig = sys.stdout
    sys.stdout   = io.StringIO()
    lexer        = MLDeclaraLexer(input_stream)
    sys.stdout   = _stdout_orig

    # Remove o listener padrão do ANTLR (que imprime em stderr)
    lexer.removeErrorListeners()
    lex_error_listener = MLDeclaraErrorListener()
    lexer.addErrorListener(lex_error_listener)

    # Verifica tokens ilegais manualmente antes do parsing
    from antlr4 import Token
    todos_tokens = []
    erro_lexico  = False

    while True:
        tok = lexer.nextToken()
        if tok.type == Token.EOF:
            todos_tokens.append(tok)
            break
        nome = MLDeclaraLexer.symbolicNames[tok.type] if tok.type < len(MLDeclaraLexer.symbolicNames) else "?"
        if nome == "ERRO_STRING":
            print(f"Erro léxico — linha {tok.line}: cadeia literal não fechada: {tok.text!r}")
            erro_lexico = True
            break
        elif nome == "ERRO_SIMBOLO":
            print(f"Erro léxico — linha {tok.line}: símbolo não identificado: {tok.text!r}")
            erro_lexico = True
            break
        todos_tokens.append(tok)

    if erro_lexico:
        return None

    # ── ETAPA 2: Análise Sintática ──────────────────────────────────
    from antlr4 import CommonTokenStream
    from antlr4.ListTokenSource import ListTokenSource

    token_source = ListTokenSource(todos_tokens)
    token_stream = CommonTokenStream(token_source)
    _stdout_orig2 = sys.stdout
    sys.stdout    = io.StringIO()
    parser        = MLDeclaraParser(token_stream)
    sys.stdout    = _stdout_orig2

    # Substitui o ErrorListener padrão pelo customizado
    parser.removeErrorListeners()
    syn_error_listener = MLDeclaraErrorListener()
    parser.addErrorListener(syn_error_listener)

    parse_tree = parser.programa()

    if syn_error_listener.tem_erros:
        print(f"\n{'─'*55}")
        print(f"  Erros sintáticos em '{nome_arquivo}':")
        print(f"{'─'*55}")
        for erro in syn_error_listener.erros:
            print(f"  {erro}")
        print(f"{'─'*55}\n")
        return None

    # ── ETAPA 3: Construção da AST ──────────────────────────────────
    builder = ASTBuilder()
    ast     = builder.visit(parse_tree)

    # ── ETAPA 4: Análise Semântica (Pessoa 2) ───────────────────────
    # Importação opcional — só ativa se o módulo existir
    try:
        from semantic import SemanticVisitor  # módulo da Pessoa 2
        semantic = SemanticVisitor()
        erros_semanticos = semantic.verificar(ast)
        if erros_semanticos:
            print(f"\n{'─'*55}")
            print(f"  Erros semânticos em '{nome_arquivo}':")
            print(f"{'─'*55}")
            for erro in erros_semanticos:
                print(f"  {erro}")
            print(f"{'─'*55}\n")
            return None
    except ImportError:
        pass  # Módulo ainda não implementado pela Pessoa 2

    # ── ETAPA 5: Geração de Código (Pessoa 3) ───────────────────────
    try:
        from codegen import CodeGenerator  # módulo da Pessoa 3
        gen    = CodeGenerator()
        codigo = gen.gerar(ast)
        print(codigo)
    except ImportError:
        pass  # Módulo ainda não implementado pela Pessoa 3

    return ast


def main():
    if len(sys.argv) < 2:
        print("Uso: python main.py <arquivo.mld>")
        sys.exit(1)

    caminho = sys.argv[1]
    try:
        with open(caminho, encoding="utf-8") as f:
            codigo = f.read()
    except FileNotFoundError:
        print(f"Arquivo não encontrado: {caminho}")
        sys.exit(1)

    resultado = compilar(codigo, caminho)
    if resultado is None:
        sys.exit(1)


if __name__ == "__main__":
    main()
