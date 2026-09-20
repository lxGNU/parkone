#!/bin/bash
# Script para compilar los archivos java
mkdir -p bin
javac -d bin -cp "lib/*" $(find src/main/java -name "*.java")
if [ $? -eq 0 ]; then
    echo "Compilacion lista."
else
    echo "Error al compilar."
    exit 1
fi
