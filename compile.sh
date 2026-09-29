#!/bin/bash
# Script para compilar los modulos java del proyecto ParkOne
mkdir -p bin
javac -d bin -cp "lib/*" $(find src/main/java -name "*.java")
if [ $? -eq 0 ]; then
    echo "================================================="
    echo " Compilacion completada con exito."
    echo "================================================="
else
    echo "Error durante la compilacion de los archivos Java."
    exit 1
fi
