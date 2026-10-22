#!/bin/bash
# Script para compilar y ejecutar las pruebas de los modulos ParkOne
./compile.sh
if [ $? -ne 0 ]; then
    echo "Error compilando el proyecto."
    exit 1
fi

javac -d bin -cp "bin:lib/*" $(find src/test/java -name "*.java")
if [ $? -eq 0 ]; then
    echo "Pruebas compiladas correctamente. Ejecutando..."
    java -cp "bin:lib/*" co.edu.sena.parkone.PruebasModulosTest
else
    echo "Error compilando las pruebas."
fi
