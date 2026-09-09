#!/bin/bash
# =============================================================================
# Script para ejecutar la aplicacion ParkOne
# Uso:
#   ./run.sh           -> Abre la ventana grafica (Swing) o la consola si no hay pantalla
#   ./run.sh --consola -> Abre el menu por consola directamente
# =============================================================================
set -e

if [ ! -d "bin" ]; then
    ./compile.sh
fi

CP="bin:lib/*"
java -cp "$CP" co.edu.sena.parkone.Principal "$@"
