@echo off
setlocal enabledelayedexpansion

:: ============================================================
:: Script de testes do T6 — ML-Declara: Análise Léxica/Sintática (Windows)
:: Executa todos os .mld em 1.validos/ (devem passar) e
:: em 2.invalidos/ (devem falhar com mensagem de erro).
::
:: Pré-requisito: pip install antlr4-python3-runtime
:: ============================================================

set PYTHON=python
set MAIN=src\main\python\br\ufscar\dc\compiladores\mldeclara\main.py
set VALIDOS=casos-de-teste\6.casos_teste_t6\1.validos
set INVALIDOS=casos-de-teste\6.casos_teste_t6\2.invalidos

if not exist "%MAIN%" (
    echo [ERRO] main.py nao encontrado em %MAIN%
    exit /b 1
)

set PASSOU=0
set FALHOU=0

echo ============================================
echo   RODANDO OS TESTES DO T6 (ML-Declara ALS)
echo ============================================
echo.

echo --- Programas VALIDOS (devem compilar sem erro) ---
for %%f in ("%VALIDOS%\*.mld") do (
    set NOME=%%~nxf
    %PYTHON% "%MAIN%" "%%f" >nul 2>&1
    if !errorlevel! equ 0 (
        set /a PASSOU+=1
        echo [OK]      !NOME!
    ) else (
        set /a FALHOU+=1
        echo [FALHOU]  !NOME!
    )
)

echo.
echo --- Programas INVALIDOS (devem falhar com erro) ---
for %%f in ("%INVALIDOS%\*.mld") do (
    set NOME=%%~nxf
    %PYTHON% "%MAIN%" "%%f" >nul 2>&1
    if !errorlevel! neq 0 (
        set /a PASSOU+=1
        echo [OK]      !NOME!
    ) else (
        set /a FALHOU+=1
        echo [FALHOU]  !NOME!  (deveria ter dado erro)
    )
)

echo.
echo ============================================
echo   RESULTADO: !PASSOU! passaram, !FALHOU! falharam
echo ============================================

endlocal
pause
