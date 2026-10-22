@echo off
rem Script para iniciar ParkOne en Windows
if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
) else if exist "C:\Program Files\Java\jdk-21.0.12.1\bin" (
    set "PATH=C:\Program Files\Java\jdk-21.0.12.1\bin;%PATH%"
)

if not exist bin (
    call compile.bat
)
echo Abrir en el navegador: http://localhost:8080/
java -cp "bin;lib/*" co.edu.sena.parkone.ParkOneApplication
pause
