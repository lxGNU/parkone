# Proyecto ParkOne - Modulos de Software (Servlets y JSP)

**Evidencia:** GA7-220501096-AA2-EV02  
**Programa:** Tecnologo en Analisis y Desarrollo de Software (ADSO)  
**Ficha:** 3336118  
**Aprendiz:** Brandon Yair Galvis Diaz  
**SENA Regional Cauca** - 2026  

---

## 1. Descripcion del Proyecto

En esta evidencia programe los modulos del sistema **ParkOne** utilizando **Java Servlets**, paginas **JSP**, **HTML** y conexion a **MySQL** por JDBC.

Se implementaron:
- Formularios HTML conectados con Servlets.
- Uso de metodos GET y POST.
- Paginas JSP con expresiones y partes reutilizables (header, menu, footer).
- Modulo de usuarios y modulo de vehiculos.

---

## 2. Estructura de Carpetas

```text
BrandonYairGalvisDiaz_AA2_EV02/
├── ENLACE_REPOSITORIO.txt
├── README.md
├── pom.xml
├── .gitignore
├── compile.sh / compile.bat
├── run.sh / run.bat
├── sql/
│   └── parkone_db.sql
└── src/
    └── main/
        ├── java/co/edu/sena/parkone/
        │   ├── conexion/
        │   │   └── ConexionBD.java
        │   ├── modelo/
        │   │   ├── Usuario.java
        │   │   └── Vehiculo.java
        │   ├── dao/
        │   │   ├── UsuarioDAO.java
        │   │   └── VehiculoDAO.java
        │   ├── servlet/
        │   │   ├── UsuarioServlet.java
        │   │   └── VehiculoServlet.java
        │   └── server/
        │       └── ServidorWeb.java
        └── webapp/
            ├── index.jsp
            ├── usuarios.jsp
            ├── formulario-usuario.jsp
            ├── vehiculos.jsp
            ├── formulario-vehiculo.jsp
            ├── includes/
            │   ├── header.jsp
            │   ├── menu.jsp
            │   └── footer.jsp
            ├── css/
            │   └── style.css
            └── WEB-INF/
                └── web.xml
```

---

## 3. Como Ejecutar

En Linux / Mac:
```bash
./compile.sh
./run.sh
```
Abrir el navegador en `http://localhost:8080/`.

En Windows:
- Doble clic en `compile.bat`
- Doble clic en `run.bat`

---

## 4. Base de Datos
El script con las tablas esta en `sql/parkone_db.sql`.
