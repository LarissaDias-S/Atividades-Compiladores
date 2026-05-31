@echo off
setlocal enabledelayedexpansion

:: ============================================================
:: Script de testes do T5 — Geracao de Codigo C + GCC (Windows)
:: Executa os 20 casos em ordem numerica (1..20) e normaliza
:: quebras de linha (CRLF -> LF) antes de comparar a saida.
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

:: Loop numerico garante ordem 1, 2, 3 ... 20 (nao 1, 10, 11, 2 ...)
for /L %%n in (1,1,20) do (
    set "ARQ_ALG="
    for %%f in ("%ENTRADA%\%%n.*.alg") do set "ARQ_ALG=%%f"

    if not defined ARQ_ALG (
        echo [AVISO] Caso %%n nao encontrado em %ENTRADA%
    ) else (
        for %%f in ("!ARQ_ALG!") do (
            set NOME=%%~nxf
            set BASE=%%~nf
            set ARQ_C=%TEMP_DIR%\!BASE!.c
            set ARQ_EXE=%TEMP_DIR%\!BASE!.exe
            set ARQ_OUT=%TEMP_DIR%\!BASE!_out.txt
            set ARQ_NORM=%TEMP_DIR%\!BASE!_norm.txt
            set ARQ_IN=%ENTRADA_EXEC%\!NOME!
            set OK=1

            echo --- !NOME! ---

            :: Passo 1: LA -^> C
            java -jar "%JAR%" "%%f" "!ARQ_C!" 2>nul
            if !errorlevel! neq 0 set OK=0

            if !OK!==1 (
                findstr /C:"#include" "!ARQ_C!" >nul 2>&1
                if !errorlevel! neq 0 (
                    set OK=0
                    echo [FALHOU] Compilador LA nao gerou codigo C:
                    type "!ARQ_C!"
                )
            )

            :: Passo 2: GCC
            if !OK!==1 (
                gcc -w "!ARQ_C!" -o "!ARQ_EXE!" 2>"%TEMP_DIR%\!BASE!_gcc_err.txt"
                if !errorlevel! neq 0 (
                    set OK=0
                    echo [FALHOU] Erro de compilacao GCC:
                    type "%TEMP_DIR%\!BASE!_gcc_err.txt"
                )
            )

            :: Passo 3: Executar
            if !OK!==1 (
                if exist "!ARQ_IN!" (
                    "!ARQ_EXE!" < "!ARQ_IN!" > "!ARQ_OUT!" 2>nul
                ) else (
                    "!ARQ_EXE!" > "!ARQ_OUT!" 2>nul
                )
            )

            :: Passo 4: Normalizar CRLF -^> LF e comparar com gabarito
            if !OK!==1 (
                powershell -NoProfile -Command ^
                    "$o=[IO.File]::ReadAllBytes('!ARQ_OUT!'); $t=New-Object System.Collections.Generic.List[byte]; foreach($b in $o){if($b -ne 13){$t.Add($b)}}; [IO.File]::WriteAllBytes('!ARQ_NORM!',$t.ToArray())" 2>nul
                if !errorlevel! neq 0 set OK=0
            )

            if !OK!==1 (
                fc /B "!ARQ_NORM!" "%GABARITO%\!NOME!" >nul 2>&1
                if !errorlevel! neq 0 (
                    set OK=0
                    echo [FALHOU] Saida diferente do esperado
                    echo   -- Esperado:
                    type "%GABARITO%\!NOME!"
                    echo   -- Obtido:
                    type "!ARQ_NORM!"
                )
            )

            if !OK!==1 (
                set /a PASSOU+=1
                echo [OK]
            ) else (
                set /a FALHOU+=1
            )
            echo.
        )
    )
)

echo.
echo ============================================
echo   RESULTADO: !PASSOU! passaram, !FALHOU! falharam (de 20)
echo ============================================

endlocal
pause
