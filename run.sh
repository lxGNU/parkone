#!/bin/bash
# Script para iniciar el servidor
if [ ! -d "bin" ]; then
    ./compile.sh
fi
echo "Abrir navegador en http://localhost:8080/"
java -cp "bin:lib/*" co.edu.sena.parkone.server.ServidorWeb
