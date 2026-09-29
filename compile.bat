@echo off
rem Script para compilar los modulos java en Windows
if not exist bin mkdir bin
for /f "tokens=*" %%i in ('dir /s /b src\main\java\*.java') do (
    javac -d bin -cp "lib/*" "%%i"
)
if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa.
) else (
    echo Error en la compilacion.
)
pause
