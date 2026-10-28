# Sistema de Gestión de Parqueaderos ParkOne - Componente Front-End

**Programa:** Tecnólogo en Análisis y Desarrollo de Software (ADSO)  
**Ficha de Caracterización:** 3336118  
**Evidencia:** GA7-220501096-AA4-EV03: Componente front-end del proyecto formativo y proyectos de clase  
**Aprendiz:** Brandon Yair Galvis Díaz  
**Centro:** Centro de Teleinformática y Producción Industrial - SENA Regional Cauca  
**Año:** 2026  

---

## 📌 1. Descripción del Proyecto

**ParkOne** es un software integral orientado al control operativo de parqueaderos comerciales y empresariales. En esta evidencia se codificó el **componente front-end interactivo**, articulando los requerimientos funcionales previamente levantados (historias de usuario HU01 a HU09, diagramas de casos de uso y modelo de datos relacional `parkone_db`).

El front-end fue construido con tecnologías web estándares (**HTML5 semántico, CSS3 modular y JavaScript ES6+**), implementando persistencia en el cliente a través de **LocalStorage**, diseño responsivo adaptable a dispositivos móviles y de escritorio, y controles rigurosos de validación de datos en tiempo real.

---

## 🚀 2. Ejecución Inmediata

1. **Opción 1 (Recomendada):** Dar doble clic en el archivo `iniciar.bat`.
2. **Opción 2:** Abrir el archivo `index.html` en cualquier navegador web moderno (Google Chrome, Microsoft Edge, Mozilla Firefox).
3. **Credenciales de prueba:**
   - **Cédula:** `1061723849`
   - **Clave:** `123456`
   - **Rol:** Administrador

---

## 📂 3. Estructura de Archivos del Proyecto

```text
GA7-220501096-AA4-EV03/
├── .git/                               # Repositorio local de control de versiones Git
├── .gitignore                          # Archivos excluidos del control de versiones
├── css/
│   └── style.css                       # Hoja de estilos global (Dark Theme + Verde SENA)
├── js/
│   ├── data.js                         # Estado global, persistencia LocalStorage y tarifas
│   ├── inventario.js                   # Lógica de taquilla, entradas, salidas y tickets
│   └── usuarios.js                     # Gestión de colaboradores y validaciones de usuario
├── images/
│   ├── logo2.png                       # Logo corporativo ParkOne
│   ├── user.jpg                        # Imagen para gestión de usuarios
│   ├── inventario.jpg                  # Imagen para control de patio
│   ├── consultas.jpg                   # Imagen para consultas analíticas
│   └── recibos.jpg                     # Imagen para comprobantes y recibos
├── index.html                          # Dashboard principal con métricas KPI y accesos rápidos
├── control.html                        # Taquilla: registro de ingresos, cobros y ticket térmico
├── usuarios.html                       # Administración de personal y roles operativos
├── vehiculos.html                      # Catálogo de categorías vehiculares y simulador
├── consultas.html                      # Módulo analítico y filtros de auditoría
├── recibos.html                        # Repositorio de comprobantes con reimpresión
├── login.html                          # Pantalla de autenticación y validación de credenciales
├── iniciar.bat                         # Lanzador rápido en Windows
├── ENLACE_REPOSITORIO.txt              # Enlaces a GitHub y credenciales
├── package.json                        # Manifiesto del proyecto y scripts
└── README.md                           # Documentación técnica
```

---

## 🛠 4. Módulos Front-End Implementados

1. **Dashboard Principal (`index.html`):**
   - 4 tarjetas KPI: Capacidad Total (50 bahías), Cupos Ocupados, Cupos Disponibles y Recaudo Acumulado Hoy.
   - Tarjetas interactivas con efectos de elevación hover (`user.jpg`, `inventario.jpg`, `consultas.jpg`, `recibos.jpg`).
   - Tabla en tiempo real de vehículos actualmente en patio con estado activo y botón de salida.

2. **Control de Parqueadero (`control.html`):**
   - Formulario de entrada con validación de placa colombiana (mínimo 5 caracteres).
   - Bloqueo de placa duplicada: impide registrar un vehículo si ya se encuentra dentro del patio.
   - Generación de **Ticket Térmico de Entrada** con opción de impresión directa (`window.print()`).
   - Modal de liquidación automática de cobro calculando horas y fracciones según categoría.
   - Generación de **Comprobante de Pago** con desglose de minutos y valor total.

3. **Gestión de Usuarios (`usuarios.html`):**
   - Formulario estructurado para registro de colaboradores con validación de cédula única y correo electrónico.
   - Vista previa de fotografía de perfil (`user.jpg`).
   - Tabla dinámica con filtrado en tiempo real y asignación de roles (Administrador y Operador).

4. **Tarifas y Vehículos (`vehiculos.html`):**
   - Catálogo de tarifas vigentes: Automóvil ($3.500/h), Moto ($2.000/h), Camioneta ($4.500/h), Bicicleta ($1.000/h).
   - Simulador interactivo que calcula el costo en pesos según horas y minutos.

5. **Consultas y Reportes (`consultas.html`):**
   - Filtros combinados por texto de placa, categoría vehicular y estado operativo.
   - Resumen cuantitativo de movimientos, recaudo y ticket promedio.

6. **Recibos de Pago (`recibos.html`):**
   - Historial completo de recibos expedidos con formato térmico listo para reimpresión.

7. **Inicio de Sesión (`login.html`):**
   - Control de acceso con verificación en LocalStorage y redirección al Dashboard.

---

## 🌿 5. Control de Versiones (Git)

- **Repositorio Oficial:** [https://github.com/lxGNU/parkone](https://github.com/lxGNU/parkone)
- **Rama Front-End:** `feature/front-end-react`
- **Convención de Commits:** Commits semánticos (`feat:`, `fix:`, `docs:`, `style:`).
