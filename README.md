# Proyecto ParkOne - Modulos Codificados y Probados

**Evidencia:** GA7-220501096-AA3-EV02  
**Programa:** Tecnologo en Analisis y Desarrollo de Software (ADSO)  
**Ficha:** 3336118  
**Aprendiz:** Brandon Yair Galvis Diaz  
**SENA Regional Cauca** - 2026  

---

## Descripcion

En esta evidencia se realizaron las pruebas funcionales de los modulos del software ParkOne:
- Modulo Stand-alone (consola con pruebas de logica de negocio y validaciones).
- Modulo Web (panel de control, entradas, salidas, liquidacion de horas y cobro).
- Modulo Movil y API REST (endpoints JSON para aplicaciones moviles).

---

## Como Ejecutar las Pruebas y la Aplicacion

### En Linux / Mac:
```bash
chmod +x compile.sh run.sh test.sh
./compile.sh   # Compila el codigo
./test.sh      # Ejecuta las pruebas unitarias y validaciones
./run.sh       # Inicia el servidor web
```

### En Windows:
- Doble clic en `compile.bat`
- Doble clic en `test.bat` (muestra el resultado de las 8 pruebas en consola)
- Doble clic en `run.bat` (inicia el servidor)

Abrir en el navegador: `http://localhost:8080/`

Credenciales de prueba:
- Cedula: `1061723849`
- Clave: `123456`

---

## Estructura del Proyecto

```text
BrandonYairGalvisDiaz_AA3_EV02/
├── ENLACE_REPOSITORIO.txt
├── README.md
├── compile.sh / compile.bat
├── test.sh / test.bat
├── run.sh / run.bat
├── sql/
│   └── parkone_db.sql
├── src/
│   ├── main/java/co/edu/sena/parkone/  (Codigo del proyecto)
│   └── test/java/co/edu/sena/parkone/  (Pruebas de validaciones)
└── EVIDENCIA_GA7_220501096_AA3_EV02_BRANDON_GALVIS.docx
```
