#!/bin/bash
# Script para iniciar la aplicacion ParkOne Framework
if [ ! -d "bin" ]; then
    ./compile.sh
fi
echo "================================================="
echo " Iniciando ParkOne Framework..."
echo " Abrir en navegador: http://localhost:8080/"
echo "================================================="
java -cp "bin:lib/*" co.edu.sena.parkone.ParkOneApplication
