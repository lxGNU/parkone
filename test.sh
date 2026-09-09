#!/bin/bash
# =============================================================================
# Script para ejecutar las pruebas del CRUD
# Aprendiz: Brandon Yair Galvis Diaz - SENA ADSO
# =============================================================================
set -e

if [ ! -d "bin" ]; then
    ./compile.sh
fi

CP="bin:lib/*"
java -cp "$CP" co.edu.sena.parkone.test.PruebasCRUD
