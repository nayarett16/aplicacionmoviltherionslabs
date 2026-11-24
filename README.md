# Gestión de Recursos Institucionales - TherionLabs

Aplicación móvil desarrollada en Android Studio (Kotlin + Jetpack Compose) conectada a una arquitectura distribuida de microservicios en Spring Boot (desplegada en Render con base de datos PostgreSQL en Supabase y enrutada mediante Spring Cloud Gateway) para la solicitud, control y aprobación de recursos institucionales en sede Duoc UC, permitiendo la interacción en tiempo real entre la comunidad docente, coordinadores de extensión y áreas proveedoras de recursos.

---

## Autores / Integrantes
- **Antonella Valdivia** ([@anto589](https://github.com/anto589))
- **Nayarett Vergara** ([@nayarett16](https://github.com/nayarett16))

---

## Características Principales
- **Autenticación por roles conectada a microservicios:** Acceso diferenciado validado contra `usuarios-service` para Docentes/Comunidad (`comunidad@duocuc.cl`), Coordinador de Extensión (`extension@duocuc.cl`) y Áreas Aprobadoras (`cta@duocuc.cl`, `comunicaciones@duocuc.cl`, `servicios@duocuc.cl`, `porteria@duocuc.cl`).
- **Gestión de solicitudes de recursos en la nube y agenda de sede:** Formulario interactivo para solicitar equipamiento y materiales institucionales con validaciones en tiempo real (mínimo de anticipación, restricción de domingos y feriados nacionales) sincronizado con Supabase PostgreSQL y calendario interactivo de disponibilidad.
- **Control de inventario y stock por área:** Visualización en tiempo real del inventario y stock disponible por cada entidad responsable (`CTA`, `Comunicaciones`, `Servicios Generales`, `Portería`).
- **Integración meteorológica (API Externa):** Consulta del clima en tiempo real mediante **Open-Meteo** para apoyar la planificación de actividades institucionales con recursos al aire libre.
- **Persistencia híbrida (Cloud + Local):** Sincronización remota mediante **Retrofit** + **Spring Cloud Gateway** y respaldo local mediante `SharedPreferences` (`PreferenciasManager`).
- **Recursos nativos:** Integración de respuesta háptica (`HapticFeedback`) al interactuar con botones y notificaciones locales del sistema (`NotificationHelper`) tras enviar solicitudes.
- **Interfaz dinámica y fluida:** Uso de animaciones compuestas (`AnimatedVisibility`, `AnimatedContent`) para la transición de estados (`Aprobado`, `Rechazado`, `Pendiente`).

---

## Arquitectura y Estructura del Proyecto (MVVM + Microservicios)
El proyecto aplica el patrón de arquitectura **Model-View-ViewModel (MVVM)** en Android integrado con una arquitectura de microservicios en Spring Boot:
- **`network/`**: Cliente HTTP centralizado con **Retrofit** y **OkHttp** (`RetrofitClient`, `UsuariosApiService`, `ApiService`, `SolicitudesApiService`) apuntando a Spring Cloud Gateway (`https://therionlabs-gateway.onrender.com/`).
- **`model/`**: Modelos de datos centralizados y DTOs (`SolicitudActividad`, `SolicitudDto`, `RecursoModel`).
- **`ui/viewmodel/`**: Lógica de negocio, consumo de APIs y validaciones desacopladas de la UI (`LoginViewModel`, `SolicitudViewModel`).
- **`ui/screens/`**: Vistas desarrolladas en Jetpack Compose (`LoginScreen`, `ProfesorHomeScreen`, `CoordinadorHomeScreen`, `EntidadHomeScreen`, `DetalleSolicitudScreen`, `NuevaSolicitudScreen`, `CalendarioScreen`).
- **`data/`**: Repositorios remotos/locales y recursos nativos (`RepositorioSolicitudes`, `RepositorioRecursos`, `PreferenciasManager`, `NotificationHelper`).

---

## Endpoints Utilizados (Microservicios y API Externa)

### 1. Microservicios Propios (Spring Cloud Gateway + Render + Supabase)
Todas las peticiones móviles se centralizan a través de **Spring Cloud Gateway** (`https://therionlabs-gateway.onrender.com/`):

| Servicio | Puerto Local | URL Producción (Render) | Método | Endpoint | Descripción |
| :--- | :---: | :--- | :---: | :--- | :--- |
| **API Gateway** | `8080` | `https://therionlabs-gateway.onrender.com` | — | `/api/**` | Punto único de entrada y enrutamiento |
| **Usuarios Service** | `8081` | `https://usuarios-service-....onrender.com` | `POST` | `/api/usuarios/login` | Validación de credenciales y rol institucional |
| **Usuarios Service** | `8081` | `https://usuarios-service-....onrender.com` | `GET` | `/api/usuarios` | Consulta de usuarios registrados |
| **Catálogo Service** | `8082` | `https://catalogo-service-....onrender.com` | `GET` | `/api/catalogo/recursos` | Listado de recursos y stock disponible |
| **Catálogo Service** | `8082` | `https://catalogo-service-....onrender.com` | `GET` | `/api/catalogo/recursos/area/{area}` | Filtrado de inventario de recursos por área |
| **Solicitudes Service** | `8083` | `https://solicitudes-service-enlb.onrender.com` | `GET` | `/api/solicitudes` | Obtención de solicitudes desde Supabase |
| **Solicitudes Service** | `8083` | `https://solicitudes-service-enlb.onrender.com` | `GET` | `/api/solicitudes/usuario/{correo}` | Consulta de solicitudes por correo |
| **Solicitudes Service** | `8083` | `https://solicitudes-service-enlb.onrender.com` | `POST` | `/api/solicitudes` | Creación y registro de solicitud de recursos |
| **Solicitudes Service** | `8083` | `https://solicitudes-service-enlb.onrender.com` | `PUT` | `/api/solicitudes/{id}` | Actualización de estado (`APROBADO` / `RECHAZADO`) |
| **Solicitudes Service** | `8083` | `https://solicitudes-service-enlb.onrender.com` | `DELETE` | `/api/solicitudes/{id}` | Eliminación de solicitud |

### 2. API Externa (Open-Meteo — Pronóstico del Clima)
- **URL Base:** `https://api.open-meteo.com/`
- **Endpoint:** `GET /v1/forecast?latitude=-33.4489&longitude=-70.6693&current_weather=true`
- **Propósito:** Consulta de condiciones meteorológicas actuales para evaluar el uso de recursos institucionales en actividades exteriores.

---

## Tecnologías Utilizadas y Colaboración
- **Frontend Móvil:** Kotlin, Jetpack Compose, Material Design 3, Retrofit 2, OkHttp, Coroutines / StateFlow.
- **Pruebas Unitarias:** JUnit 4, MockK, Kotlinx Coroutines Test (Cobertura > 80% en capa ViewModel).
- **Backend y Nube:** Java / Spring Boot, Spring Cloud Gateway, Render Cloud, PostgreSQL (Supabase).
- **Persistencia Local y Nativos:** SharedPreferences (Gson), Android Notification Manager, LocalHapticFeedback.
- **Control de Versiones y Planificación:** Git / GitHub (repositorios colaborativos móvil y microservicios) y planificación Kanban en **Trello**.

---

## Requisitos Previos
- Android Studio Ladybug / Iguana / Jellyfish o superior.
- JDK 17 configurado en el IDE.
- Emulador Android o Dispositivo Físico con **Android 8.0 (API 26)** o superior.

---

## Pasos para Ejecutar la Aplicación

### Modo 1: Ejecución Directa Conectada a Producción (Render + Supabase)
La aplicación ya está configurada en `RetrofitClient.kt` para consumir directamente el Gateway desplegado en Render (`https://therionlabs-gateway.onrender.com/`):

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/nayarett16/aplicaciónmóviltherionslabs.git](https://github.com/nayarett16/aplicaciónmóviltherionslabs.git)