@echo off
setlocal enabledelayedexpansion

:: ============================================================
:: Script de testes do T5 — Geracao de Codigo C + GCC
:: Compila cada .alg em C, compila com GCC, executa e valida
:: a saida contra o gabarito em 4.saida/
:: ============================================================

set JAR=target\meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar
set ENTRADA=casos-de-teste\5.casos_teste_t5\1.entrada
set ENTRADA_EXEC=casos-de-teste\5.casos_teste_t5\3.entrada_execucao
set GABARITO=casos-de-teste\5.casos_teste_t5\4.saida
set TEMP_DIR=%TEMP%\t5_gcc_testes

if not exist "%JAR%" (
    echo [ERRO] JAR nao encontrado. Execute "mvn clean package" antes.
    exit /b 1
)

if not exist "%TEMP_DIR%" mkdir "%TEMP_DIR%"

set PASSOU=0
set FALHOU=0

echo ============================================
echo   RODANDO OS 20 TESTES DO T5 (Gerador C + GCC)
echo ============================================
echo.

for %%f in ("%ENTRADA%\*.alg") do (
    set NOME=%%~nxf
    set BASE=%%~nf
    set ARQ_C=%TEMP_DIR%\!BASE!.c
    set ARQ_EXE=%TEMP_DIR%\!BASE!.exe
    set ARQ_OUT=%TEMP_DIR%\!BASE!_out.txt
    set ARQ_IN=%ENTRADA_EXEC%\!NOME!
    set OK=1

    echo --- !NOME! ---

    java -jar "%JAR%" "%%f" "!ARQ_C!" 2>nul
    if !errorlevel! neq 0 set OK=0

    if !OK!==1 findstr /C:"#include" "!ARQ_C!" >nul 2>&1
    if !OK!==1 if !errorlevel! neq 0 set OK=0

    if !OK!==1 (
        gcc -w "!ARQ_C!" -o "!ARQ_EXE!" 2>"%TEMP_DIR%\!BASE!_gcc_err.txt"
        if !errorlevel! neq 0 set OK=0
    )

    if !OK!==1 (
        if exist "!ARQ_IN!" (
            "!ARQ_EXE!" < "!ARQ_IN!" > "!ARQ_OUT!" 2>nul
        ) else (
            "!ARQ_EXE!" > "!ARQ_OUT!" 2>nul
        )
        fc /B "!ARQ_OUT!" "%GABARITO%\!NOME!" >nul 2>&1
        if !errorlevel! neq 0 set OK=0
    )

    if !OK!==1 (
        set /a PASSOU+=1
        echo [OK]
    ) else (
        set /a FALHOU+=1
        echo [FALHOU] Verifique os arquivos em %TEMP_DIR%
    )
    echo.
)

echo.
echo ============================================
echo   RESULTADO: !PASSOU! passaram, !FALHOU! falharam (de 20)
echo ============================================

endlocal
pause
