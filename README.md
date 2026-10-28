# Componente Front-End - Sistema de Gestión de Parqueadero ParkOne

**Programa:** Tecnólogo en Análisis y Desarrollo de Software (ADSO)  
**Ficha:** 3336118  
**Evidencia:** GA7-220501096-AA4-EV03  
**Aprendiz:** Brandon Yair Galvis Díaz  
**SENA Regional Cauca - 2026**  

---

## 📌 Descripción

Este proyecto contiene la codificación del componente front-end del software formativo **ParkOne**, articulando los artefactos del ciclo de software previos (historias de usuario, diagramas de casos de uso, diseños prototipo y modelo de datos relacional).

La solución fue implementada en **HTML5 semántico, CSS3 modular (Dark theme y verde SENA) y JavaScript**, con persistencia en **LocalStorage**, comentarios descriptivos en el código y gestión bajo control de versiones con **Git**.

---

## 🚀 Ejecución Rápida

1. Dar doble clic en el archivo `iniciar.bat` o abrir `index.html` en el navegador.
2. **Credenciales de prueba:**
   - **Cédula:** `1061723849`
   - **Clave:** `123456`

---

## 📂 Estructura del Proyecto

```text
GA7-220501096-AA4-EV03/
├── .git/                    # Repositorio de control de versiones Git
├── .gitignore               # Archivos excluidos de Git
├── css/
│   └── style.css            # Estilos del sistema ParkOne (Dark theme y verde SENA)
├── js/
│   ├── data.js              # Capa de datos, estado global (LocalStorage) y tarifas
│   ├── inventario.js        # Lógica de taquilla, entradas, salidas y tickets
│   └── usuarios.js          # Lógica de gestión de usuarios y colaboradores
├── images/                  # Recursos gráficos del sistema ParkOne
├── index.html               # Panel Principal / Dashboard con estadísticas en tiempo real
├── control.html             # Módulo de Control de Parqueadero (Entradas, Salidas y Cobro)
├── usuarios.html            # Módulo de Gestión de Usuarios y Colaboradores
├── vehiculos.html           # Módulo de Catálogo Vehicular y Tarifas Vigentes
├── login.html               # Módulo de Inicio de Sesión de Operadores
├── iniciar.bat              # Script para ejecución rápida con doble clic
├── ENLACE_REPOSITORIO.txt   # Enlaces del repositorio GitHub y credenciales
├── package.json             # Manifiesto del proyecto
└── README.md                # Documentación técnica
```

---

## 🛠 Módulos Desarrollados

1. **Panel Principal (`index.html`):** 4 tarjetas de estadísticas dinámicas (Capacidad Total 50 bahías, Ocupados, Libres y Recaudo) y tabla en vivo de vehículos activos en patio.
2. **Control Parqueadero (`control.html`):** Formulario de registro de entrada con validación de placa no duplicada en patio, emisión de ticket térmico imprimible y liquidación automática de cobro de salida.
3. **Gestión de Vehículos (`vehiculos.html`):** Catálogo de tarifas vigentes por categoría (Carro $3.500/h, Moto $2.000/h, Camioneta $4.500/h, Bici $1.000/h) y tabla de vehículos.
4. **Gestión de Usuarios (`usuarios.html`):** Registro y consulta de personal con asignación de roles (Administrador y Operador).
5. **Inicio de Sesión (`login.html`):** Autenticación y control de acceso con LocalStorage.

---

## 🌿 Control de Versiones (Git)

- **Repositorio:** [https://github.com/lxGNU/parkone](https://github.com/lxGNU/parkone)
- **Rama:** `feature/front-end-react`
