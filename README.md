# Reserva de Espacios - TherionLabs

Aplicación móvil desarrollada en Android Studio para la gestión, control y aprobación de reservas de espacios y recursos institucionales en sede Duoc UC, permitiendo la interacción entre la comunidad docente, coordinadores de extensión y áreas proveedoras de recursos.

## 👥 Autores / Integrantes
- **Antonella Valdivia** ([@anto589](https://github.com/anto589))
- **Nayarett Vergara** ([@nayarett16](https://github.com/nayarett16))

## 🚀 Características Principales
- **Autenticación por roles:** Acceso diferenciado para Docentes/Comunidad, Coordinador de Sede y Áreas Aprobadoras (CTA, Comunicaciones, Servicios Generales, Portería).
- **Gestión de solicitudes:** Formulario interactivo para creación de eventos con validaciones en tiempo real (mínimo de anticipación, restricción de domingos y feriados nacionales).
- **Control de inventario y stock:** Visualización en tiempo real del inventario y stock disponible por cada entidad responsable.
- **Persistencia local:** Almacenamiento y recuperación de solicitudes mediante `SharedPreferences` (`PreferenciasManager`).
- **Recursos nativos:** Integración de respuesta háptica (`HapticFeedback`) al interactuar con botones y notificaciones locales del sistema (`NotificationHelper`) tras enviar solicitudes.
- **Interfaz dinámica y fluida:** Uso de animaciones compuestas (`AnimatedVisibility`, `AnimatedContent`) para la transición de estados ("Aprobado", "Rechazado", "Pendiente").

## 🏗️ Arquitectura y Estructura del Proyecto (MVVM)
El proyecto aplica el patrón de arquitectura **Model-View-ViewModel (MVVM)** para garantizar la separación de responsabilidades y la mantenibilidad del código:
- **`model/`**: Modelos de datos centralizados (`SolicitudActividad`, `RecursoModel`, `ReservaModel`).
- **`ui/viewmodel/`**: Lógica de negocio y validaciones desacopladas de la UI (`LoginViewModel`).
- **`ui/screens/`**: Vistas desarrolladas en Jetpack Compose (`LoginScreen`, `ProfesorHomeScreen`, `CoordinadorHomeScreen`, `EntidadHomeScreen`, `NuevaSolicitudScreen`, `CalendarioScreen`).
- **`data/`**: Capa de persistencia local y recursos nativos (`PreferenciasManager`, `NotificationHelper`, `RepositorioRecursos`).

## 🛠️ Tecnologías Utilizadas y Colaboración
- **Lenguaje:** Kotlin
- **Interfaz Visual:** Jetpack Compose / Material Design 3
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Persistencia de Datos:** SharedPreferences (JSON / Gson)
- **Recursos Nativos:** Android Notification Manager, LocalHapticFeedback
- **Control de Versiones y Planificación:** Git / GitHub (trabajo en rama `feature/login`) y planificación del proyecto en **Trello**.

## 📱 Requisitos Previos
- Android Studio Ladybug / Iguana / Jellyfish o superior.
- JDK 17 configurado en el IDE.
- Emulador Android o Dispositivo Físico con **Android 8.0 (API 26)** o superior.

## ⚙️ Pasos para Ejecutar la Aplicación

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/nayarett16/aplicaci%C3%B3nm%C3%B3viltherionslabs.git](https://github.com/nayarett16/aplicaci%C3%B3nm%C3%B3viltherionslabs.git)