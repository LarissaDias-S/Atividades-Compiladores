@echo off
setlocal enabledelayedexpansion

:: Caminho relativo para o JAR do compilador
set JAR=target\meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar

:: Caminhos relativos para os casos de teste do T4
set ENTRADA=casos-de-teste\4.casos_teste_t4\entrada
set GABARITO=casos-de-teste\4.casos_teste_t4\saida
set SAIDA_TEMP=%TEMP%\saida_compilador_t4.txt

set PASSOU=0
set FALHOU=0

echo ============================================
echo   RODANDO OS 9 TESTES DO T4 (Analisador Semantico - Parte 2)
echo ============================================
echo.

for %%f in ("%ENTRADA%\*.txt") do (
    set NOME=%%~nxf
    java -jar "%JAR%" "%%f" "%SAIDA_TEMP%" 2>nul

    :: O /W no fc ignora diferenças de espacos em branco continuos, focando no texto do erro
    fc "%SAIDA_TEMP%" "%GABARITO%\!NOME!" /W >nul 2>&1
    if !errorlevel! == 0 (
        set /a PASSOU+=1
        echo [OK]     !NOME!
    ) else (
        set /a FALHOU+=1
        echo [FALHOU] !NOME!
        echo   -- Esperado:
        type "%GABARITO%\!NOME!"
        echo   -- Obtido:
        type "%SAIDA_TEMP%"
        echo.
    )
)

echo.
echo ============================================
echo   RESULTADO: !PASSOU! passaram, !FALHOU! falharam
echo ============================================

endlocal
pause