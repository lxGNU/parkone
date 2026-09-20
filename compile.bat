@echo off
rem Script para compilar el proyecto
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin -cp "lib/*" src\main\java\co\edu\sena\parkone\modelo\*.java src\main\java\co\edu\sena\parkone\conexion\*.java src\main\java\co\edu\sena\parkone\dao\*.java src\main\java\co\edu\sena\parkone\servlet\*.java src\main\java\co\edu\sena\parkone\server\*.java
if %errorlevel% equ 0 (
    echo Compilacion lista.
) else (
    echo Error al compilar.
    pause
)
