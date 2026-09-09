# Proyecto ParkOne - Codificación del Módulo de Usuarios
**Evidencia:** GA7-220501096-AA2-EV01: Codificación de módulos del software  
**Programa:** Tecnólogo en Análisis y Desarrollo de Software (ADSO)  
**Ficha:** 3336118  
**Centro:** Centro de Teleinformática y Producción Industrial - SENA Regional Cauca  
**Aprendiz:** Brandon Yair Galvis Diaz  
**Año:** 2026  

---

## 1. Introducción
Para este taller realicé la codificación del **Módulo de Gestión de Usuarios y Colaboradores** de nuestro proyecto de parqueadero, **ParkOne**. 

Tomé como base lo que veníamos trabajando en las evidencias anteriores:
- El diseño de la interfaz web que hice en el archivo `usuarios.html` (con los campos de nombres, apellidos, cédula, correo, teléfono y rol).
- La estructura de la base de datos que creamos en MySQL (`parkone_db.sql`) donde definimos la tabla `usuario`.
- El componente formativo del SENA sobre conexión a bases de datos con Java mediante **JDBC** (Java Database Connectivity).

El programa permite realizar todas las operaciones básicas de una base de datos (**CRUD**): registrar nuevos usuarios, consultar la lista completa, buscar por número de cédula, modificar datos y eliminar registros.

---

## 2. Estándares de Codificación Utilizados

Para cumplir con lo solicitado en la guía del taller, apliqué las normas y buenas prácticas de Java:

1. **Nombramiento de paquetes (todo en minúsculas):**
   - `co.edu.sena.parkone.modelo`: Guarda la clase molde con los datos del usuario.
   - `co.edu.sena.parkone.dao`: Contiene la clase con las consultas SQL y métodos de base de datos.
   - `co.edu.sena.parkone.conexion`: Contiene la clase que hace el puente con MySQL usando JDBC.
   - `co.edu.sena.parkone.vista`: Contiene la pantalla gráfica (Swing) y el menú por consola.

2. **Nombramiento de clases (PascalCase - Primera letra en mayúscula):**
   - `Usuario`, `UsuarioDAO`, `ConexionBD`, `FormularioUsuario`, `MenuConsola`, `Principal`.

3. **Nombramiento de métodos (camelCase - Primera letra en minúscula y verbos de acción):**
   - `insertar()`, `listar()`, `buscarPorIdentificacion()`, `actualizar()`, `eliminar()`, `getConexion()`.

4. **Nombramiento de variables y atributos (camelCase):**
   - `idUsuario`, `identificacion`, `nombres`, `apellidos`, `correo`, `celular`, `rol`, `contrasena`.

---

## 3. Estructura del Proyecto

Organice el código en carpetas para que quede separado y fácil de entender:

```text
senaproyecto/
├── src/
│   ├── main/
│   │   └── java/co/edu/sena/parkone/
│   │       ├── conexion/
│   │       │   └── ConexionBD.java       <- Conecta Java con MySQL mediante JDBC
│   │       ├── modelo/
│   │       │   └── Usuario.java          <- Clase con los atributos, getters y setters
│   │       ├── dao/
│   │       │   └── UsuarioDAO.java       <- Metodos SQL para Insertar, Consultar, Modificar y Borrar
│   │       ├── vista/
│   │       │   ├── FormularioUsuario.java<- Ventana grafica en Java Swing
│   │       │   └── MenuConsola.java      <- Menu interactivo por terminal
│   │       └── Principal.java            <- Clase principal (Main) que arranca el programa
│   └── test/
│       └── java/co/edu/sena/parkone/test/
│           └── PruebasCRUD.java          <- Script para verificar que todo el CRUD funcione
├── sql/
│   └── parkone_db.sql                   <- Script para crear la base de datos y tablas en MySQL
├── lib/                                  <- Conector oficial de MySQL (mysql-connector-j)
├── ENLACE_REPOSITORIO.txt                <- Archivo con el enlace del repositorio de GitHub
├── compile.sh / compile.bat              <- Scripts para compilar facilmente
├── run.sh / run.bat                      <- Scripts para ejecutar el programa
└── test.sh / test.bat                    <- Scripts para correr las pruebas del CRUD
```

---

## 4. Funcionalidades del CRUD Desarrolladas

1. **Inserción (Create):** Permite registrar a una persona en la tabla `usuario`. Usa `PreparedStatement` para pasar los parámetros de forma segura y recupera el ID generado automáticamente por la base de datos.
2. **Consulta (Read):** 
   - Consulta general: Trae todos los registros de la tabla y los muestra ordenados en una lista o en la tabla visual.
   - Consulta por cédula: Busca a un usuario específico digitando su número de documento.
3. **Actualización (Update):** Permite modificar el nombre, apellidos, teléfono, correo o rol de un usuario ya existente buscando por su ID.
4. **Eliminación (Delete):** Borra un usuario de la base de datos previa confirmación.

---

## 5. Cómo Ejecutar el Proyecto

### Opción A: Desde NetBeans o Eclipse
1. Abrir NetBeans o Eclipse.
2. Ir a **Archivo -> Abrir Proyecto** y seleccionar esta carpeta.
3. Asegurarse de tener agregado el archivo `mysql-connector-j-8.3.0.jar` que está en la carpeta `lib/` a las librerías del proyecto.
4. Dar clic derecho en `Principal.java` y elegir **Run File** (Ejecutar).

### Opción B: Por Consola / Terminal
En Linux / Mac:
```bash
# 1. Compilar los archivos:
./compile.sh

# 2. Ejecutar las pruebas del CRUD:
./test.sh

# 3. Abrir la ventana gráfica:
./run.sh

# 4. O si prefiere usar el menú por terminal:
./run.sh --consola
```

En Windows:
- Doble clic en `compile.bat` para compilar.
- Doble clic en `test.bat` para ver las pruebas del CRUD.
- Doble clic en `run.bat` para abrir la aplicación.

---

## 6. Base de Datos en MySQL
Para probar con MySQL local:
1. Abrir **XAMPP** o **MySQL Workbench** e iniciar el servicio de MySQL.
2. Abrir el archivo `sql/parkone_db.sql` y ejecutarlo para crear la base de datos `parkone_db` con sus tablas y datos iniciales de prueba.
3. El usuario configurado por defecto es `root` sin contraseña en el puerto `3306`.
