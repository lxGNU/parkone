@echo off
rem Script para compilar y ejecutar pruebas en Windows
if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else if exist "C:\Program Files\Java\jdk-21.0.12.1\bin" (
    set "PATH=C:\Program Files\Java\jdk-21.0.12.1\bin;%PATH%"
)

call compile.bat
if %errorlevel% neq 0 (
    echo Error compilando el proyecto.
    pause
    exit /b %errorlevel%
)

javac -d bin -cp "bin;lib/*" src/test/java/co/edu/sena/parkone/*.java
if %errorlevel% equ 0 (
    echo Pruebas compiladas. Ejecutando...
    java -cp "bin;lib/*" co.edu.sena.parkone.PruebasModulosTest
) else (
    echo Error compilando las pruebas.
)
pause
