@echo off
REM =============================================================================
REM Script para ejecutar la aplicacion en Windows
REM Aprendiz: Brandon Yair Galvis Diaz - SENA ADSO
REM =============================================================================
if not exist "bin" (
    call compile.bat
)

java -cp "bin;lib/*" co.edu.sena.parkone.Principal %*
pause
