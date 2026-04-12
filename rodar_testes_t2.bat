@echo off
setlocal enabledelayedexpansion

set JAR=C:\Users\User\Atividades-Compiladores\target\meu-compilador-1.0-SNAPSHOT-jar-with-dependencies.jar
set ENTRADA=E:\Downloads\casos-de-teste\2.casos_teste_t2\entrada
set GABARITO=E:\Downloads\casos-de-teste\2.casos_teste_t2\saida
set SAIDA_TEMP=%TEMP%\saida_compilador.txt

set PASSOU=0
set FALHOU=0

echo ============================================
echo   RODANDO 62 TESTES DO T2
echo ============================================
echo.

for %%f in ("%ENTRADA%\*.txt") do (
    set NOME=%%~nxf
    java -jar "%JAR%" "%%f" "%SAIDA_TEMP%" 2>nul

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
