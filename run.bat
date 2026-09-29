@echo off
rem Script para iniciar ParkOne en Windows
if not exist bin (
    call compile.bat
)
echo Abrir en el navegador: http://localhost:8080/
java -cp "bin;lib/*" co.edu.sena.parkone.ParkOneApplication
pause
