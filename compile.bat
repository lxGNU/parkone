@echo off
REM =============================================================================
REM Script de Compilación para Windows - ParkOne (SENA)
REM =============================================================================
echo === Compilando el proyecto ParkOne ===
if not exist "bin" mkdir bin

javac -encoding UTF-8 -cp "lib/*" -d bin src\main\java\co\edu\sena\parkone\*.java src\main\java\co\edu\sena\parkone\model\*.java src\main\java\co\edu\sena\parkone\dao\*.java src\main\java\co\edu\sena\parkone\util\*.java src\main\java\co\edu\sena\parkone\service\*.java src\main\java\co\edu\sena\parkone\view\*.java src\test\java\co\edu\sena\parkone\test\*.java

xcopy /s /y src\main\resources\* bin\ >nul 2>&1

echo === Compilacion exitosa en directorio 'bin\' ===
pause
