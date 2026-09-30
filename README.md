# Proyecto ParkOne - Modulos Web y Movil

**Evidencia:** GA7-220501096-AA3-EV01  
**Programa:** Tecnologo en Analisis y Desarrollo de Software (ADSO)  
**Ficha:** 3336118  
**Aprendiz:** Brandon Yair Galvis Diaz  
**SENA Regional Cauca** - 2026  

---

## Descripcion

En esta evidencia programe los modulos del sistema ParkOne para la gestion del parqueadero. Se usaron clases Java para los modelos, servicios y controladores web, asi como rutas de API REST en formato JSON para que se puedan consumir desde una aplicacion movil.

Modulos creados:
- Control de entradas y salidas de vehiculos (calculo de horas y cobro).
- Registro de vehiculos y propietarios.
- Gestion de usuarios y roles.
- API REST para conectar con la app movil.

---

## Estructura de Carpetas

Nota: usando el comando **tree** en Gnu/Linux (mx linux 25)

```text
ParkOne/
├── ENLACE_REPOSITORIO.txt
├── README.md
├── pom.xml
├── compile.sh / compile.bat
├── run.sh / run.bat
├── sql/
│   └── parkone_db.sql
└── src/
    └── main/
        ├── java/co/edu/sena/parkone/
        │   ├── ParkOneApplication.java
        │   ├── model/
        │   ├── repository/
        │   ├── service/
        │   ├── controller/
        │   └── server/
        └── resources/
            └── static/css/
                └── style.css
```

---

## Como Ejecutar

En Linux / Mac:
```bash
./compile.sh
./run.sh
```

En Windows:
- Doble clic en `compile.bat`
- Doble clic en `run.bat`

Abrir el navegador en `http://localhost:8080/`

---

## Base de Datos
El script SQL para MySQL esta en `sql/parkone_db.sql`.
