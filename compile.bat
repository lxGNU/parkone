@echo off
rem Script para compilar los modulos java en Windows
if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else if exist "C:\Program Files\Java\jdk-21.0.12.1\bin" (
    set "PATH=C:\Program Files\Java\jdk-21.0.12.1\bin;%PATH%"
)

if not exist bin mkdir bin
javac -d bin -cp "bin;lib/*" src\main\java\co\edu\sena\parkone\*.java src\main\java\co\edu\sena\parkone\model\*.java src\main\java\co\edu\sena\parkone\repository\*.java src\main\java\co\edu\sena\parkone\service\*.java src\main\java\co\edu\sena\parkone\controller\api\*.java src\main\java\co\edu\sena\parkone\server\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa.
) else (
    echo Error en la compilacion.
)
pause
