# ParkOne - Componente Front-End

Proyecto formativo del programa Tecnólogo en Análisis y Desarrollo de Software (ADSO).
Evidencia: GA7-220501096-AA4-EV03.
Aprendiz: Brandon Yair Galvis Díaz
Ficha: 3336118
SENA Regional Cauca - Popayán, 2026

## Descripción del Proyecto

Este proyecto contiene la codificación del componente front-end para el sistema de parqueadero ParkOne. Toma como base los módulos, historias de usuario y el diseño desarrollados en las evidencias anteriores (fase GA6 y evidencia GA7-AA3-EV02).

La aplicación fue desarrollada utilizando HTML5, CSS3 y JavaScript, permitiendo registrar la entrada de vehículos, calcular el cobro de la salida según el tiempo de permanencia, validar que no se ingresen placas repetidas, emitir tickets e imprimir comprobantes, además de gestionar los usuarios y las tarifas del parqueadero.

Los datos se almacenan en el navegador mediante LocalStorage para que la información se mantenga al recargar o navegar entre las diferentes páginas.

## Estructura de Archivos

- `index.html`: Panel principal con las tarjetas de cupos ocupados, libres, capacidad y dinero recaudado.
- `control.html`: Módulo operativo de control de entradas, salidas, cobro de tarifas y ticket térmico.
- `vehiculos.html`: Catálogo de tarifas por hora según el tipo de vehículo (carro, moto, camioneta y bicicleta).
- `usuarios.html`: Formulario para registrar colaboradores y tabla con roles de Administrador u Operador.
- `login.html`: Pantalla de inicio de sesión con validación de credenciales.
- `css/style.css`: Hoja de estilos de la aplicación (tema oscuro y verde institucional del SENA).
- `js/data.js`: Archivo con los datos iniciales y las funciones para guardar y leer en LocalStorage.
- `js/inventario.js`: Lógica de JavaScript para el registro de entradas, validación de placas y liquidación de salidas.
- `js/usuarios.js`: Lógica de JavaScript para el registro y consulta de colaboradores.
- `iniciar.bat`: Archivo para abrir la aplicación de forma rápida con doble clic en Windows.
- `ENLACE_REPOSITORIO.txt`: Archivo con el enlace del repositorio GitHub y las credenciales de prueba.

## Instrucciones para Ejecutar la Aplicación

1. En Windows, hacer doble clic en el archivo `iniciar.bat` o abrir el archivo `index.html` con cualquier navegador web (Google Chrome, Microsoft Edge, Mozilla Firefox).
2. Para acceder al sistema se pueden usar las credenciales de prueba:
   - Cédula: `1061723849`
   - Contraseña: `123456`

## Repositorio en GitHub

- Repositorio: https://github.com/lxGNU/parkone
- Rama: feature/front-end-react
