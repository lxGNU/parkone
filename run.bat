@echo off
rem Script para iniciar el servidor
if not exist bin (
    call compile.bat
)
echo Abrir navegador en http://localhost:8080/
java -cp "bin;lib/*" co.edu.sena.parkone.server.ServidorWeb
pause
