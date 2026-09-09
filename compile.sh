#!/bin/bash
# =============================================================================
# Script para compilar el proyecto ParkOne
# Aprendiz: Brandon Yair Galvis Diaz - SENA ADSO
# =============================================================================
set -e

echo "Compilando los archivos Java del proyecto ParkOne..."
mkdir -p bin

CP="lib/*"
javac -encoding UTF-8 -cp "$CP" -d bin $(find src -name "*.java")

echo "Compilacion terminada con exito en la carpeta bin/"
