package cl.duoc.aplicacionmoviltherionslabs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

// 1. Importación explícita del modelo centralizado
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

// 2. Importación directa de la persistencia
import cl.duoc.aplicacionmoviltherionslabs.data.PreferenciasManager

// 3. Importaciones explícitas de las pantallas y ViewModels
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.LoginScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.ProfesorHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.CoordinadorHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.EntidadHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.DetalleSolicitudScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.NuevaSolicitudScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.CalendarioScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.TipoEntidad
import cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel.LoginViewModel

import cl.duoc.aplicacionmoviltherionslabs.ui.theme.AplicacionMovilTherionsLabsTheme

class MainActivity : ComponentActivity() {

    // Permite capturar clics en notificaciones cuando la Activity ya estaba abierta
    private var intentActualState = mutableStateOf<Intent?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentActualState.value = intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentActualState.value = intent
        enableEdgeToEdge()
        setContent {
            AplicacionMovilTherionsLabsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    val context = LocalContext.current
                    val prefsManager = remember { PreferenciasManager(context) }

                    val loginViewModel: LoginViewModel = viewModel()

                    var pantallaActual by remember { mutableStateOf("login") }
                    var pantallaAnterior by remember { mutableStateOf("home_profesor") }
                    var solicitudSeleccionada by remember { mutableStateOf<SolicitudActividad?>(null) }
                    var entidadLogueada by remember { mutableStateOf<TipoEntidad?>(null) }

                    // Control de sesión y permisos para notificaciones
                    var rolActual by remember { mutableStateOf<String?>(null) } // "coordinador", "entidad", "profesor"
                    var solicitudPendienteNotificacion by remember { mutableStateOf<String?>(null) }

                    val listaSolicitudes = remember {
                        mutableStateListOf<SolicitudActividad>().apply {
                            addAll(prefsManager.cargarSolicitudes())
                        }
                    }

                    // Función centralizada para limpiar datos y volver al Login
                    val realizarCerrarSesion = {
                        loginViewModel.cerrarSesion()
                        solicitudSeleccionada = null
                        entidadLogueada = null
                        rolActual = null
                        solicitudPendienteNotificacion = null
                        pantallaActual = "login"
                    }

                    // Detectar clic desde la Notificación respetando sesión y permisos
                    val intentObservado by intentActualState
                    LaunchedEffect(intentObservado) {
                        val destino = intentObservado?.getStringExtra("PANTALLA_DESTINO")
                        val idSolicitud = intentObservado?.getStringExtra("SOLICITUD_ID")

                        if (destino == "detalle_solicitud" && !idSolicitud.isNullOrBlank()) {
                            // Limpiamos los extras para evitar re-ejecuciones al recomponer
                            intentObservado?.removeExtra("PANTALLA_DESTINO")
                            intentObservado?.removeExtra("SOLICITUD_ID")

                            if (rolActual == null) {
                                // Seguridad: Exigir Login primero y recordar la solicitud tocada
                                solicitudPendienteNotificacion = idSolicitud
                                pantallaActual = "login"
                            } else {
                                // Si ya está logueado, dirigir estrictamente según su rol
                                val encontrada = listaSolicitudes.find { it.id == idSolicitud }
                                when (rolActual) {
                                    "coordinador" -> {
                                        if (encontrada != null) {
                                            solicitudSeleccionada = encontrada
                                            pantallaAnterior = "home_coordinador"
                                            pantallaActual = "detalle_solicitud"
                                        }
                                    }
                                    "entidad" -> pantallaActual = "home_entidad"
                                    "profesor" -> pantallaActual = "home_profesor"
                                }
                            }
                        }
                    }

                    // Transiciones animadas entre todas las pantallas
                    AnimatedContent(
                        targetState = pantallaActual,
                        transitionSpec = {
                            val esSubpantalla = targetState in listOf("calendario", "detalle_solicitud", "solicitud")
                            val vieneDeSubpantalla = initialState in listOf("calendario", "detalle_solicitud", "solicitud")

                            when {
                                // Login hacia cualquier Home (efecto de entrada suave)
                                initialState == "login" -> {
                                    (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.96f, animationSpec = tween(350)))
                                        .togetherWith(fadeOut(animationSpec = tween(250)))
                                }
                                // Cerrar sesión hacia Login
                                targetState == "login" -> {
                                    fadeIn(animationSpec = tween(300))
                                        .togetherWith(fadeOut(animationSpec = tween(300)))
                                }
                                // Avanzar a Calendario, Detalle o Nueva Solicitud (desliza desde la derecha)
                                esSubpantalla -> {
                                    (slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeIn(tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth / 3 } + fadeOut(tween(250)))
                                }
                                // Volver hacia atrás (desliza desde la izquierda)
                                vieneDeSubpantalla -> {
                                    (slideInHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth / 3 } + fadeIn(tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeOut(tween(250)))
                                }
                                // Cualquier otro cambio
                                else -> {
                                    fadeIn(animationSpec = tween(300)).togetherWith(fadeOut(animationSpec = tween(300)))
                                }
                            }
                        },
                        label = "NavegacionPrincipalAnimada"
                    ) { pantalla ->
                        when (pantalla) {
                            "login" -> {
                                LoginScreen(
                                    loginViewModel = loginViewModel,
                                    onLoginSuccess = { correo ->
                                        val correoLibreria = correo.lowercase().trim()
                                        val entidad = TipoEntidad.desdeCorreo(correoLibreria)

                                        // Recuperamos si venía desde una notificación
                                        val solicitudPendiente = solicitudPendienteNotificacion?.let { id ->
                                            listaSolicitudes.find { it.id == id }
                                        }
                                        solicitudPendienteNotificacion = null

                                        when {
                                            correoLibreria.contains("extension") ||
                                                    correoLibreria.contains("coordinacion") ||
                                                    correoLibreria.contains("coordinador") -> {
                                                rolActual = "coordinador"
                                                if (solicitudPendiente != null) {
                                                    solicitudSeleccionada = solicitudPendiente
                                                    pantallaAnterior = "home_coordinador"
                                                    pantallaActual = "detalle_solicitud"
                                                } else {
                                                    pantallaActual = "home_coordinador"
                                                }
                                            }
                                            entidad != null -> {
                                                rolActual = "entidad"
                                                entidadLogueada = entidad
                                                pantallaActual = "home_entidad"
                                            }
                                            else -> {
                                                rolActual = "profesor"
                                                pantallaActual = "home_profesor"
                                            }
                                        }
                                    }
                                )
                            }

                            "home_profesor" -> {
                                ProfesorHomeScreen(
                                    solicitudes = listaSolicitudes,
                                    onNuevaSolicitudClick = {
                                        pantallaActual = "solicitud"
                                    },
                                    onVerCalendarioClick = {
                                        pantallaAnterior = "home_profesor"
                                        pantallaActual = "calendario"
                                    },
                                    onCerrarSesionClick = {
                                        realizarCerrarSesion()
                                    }
                                )
                            }

                            "home_coordinador" -> {
                                CoordinadorHomeScreen(
                                    solicitudes = listaSolicitudes,
                                    onSolicitudClick = { solicitud ->
                                        solicitudSeleccionada = solicitud
                                        pantallaAnterior = "home_coordinador"
                                        pantallaActual = "detalle_solicitud"
                                    },
                                    onAprobarSolicitud = { id ->
                                        val index = listaSolicitudes.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            val item = listaSolicitudes[index]
                                            listaSolicitudes[index] = item.copy(
                                                estadoGeneral = "Aprobado",
                                                estadoComunicaciones = "Aprobado",
                                                estadoServiciosGenerales = "Aprobado",
                                                estadoCTA = "Aprobado",
                                                estadoPorteria = "Aprobado"
                                            )
                                            prefsManager.guardarSolicitudes(listaSolicitudes)
                                        }
                                    },
                                    onRechazarSolicitud = { id ->
                                        val index = listaSolicitudes.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            val item = listaSolicitudes[index]
                                            listaSolicitudes[index] = item.copy(
                                                estadoGeneral = "Rechazado",
                                                estadoComunicaciones = "Rechazado",
                                                estadoServiciosGenerales = "Rechazado",
                                                estadoCTA = "Rechazado",
                                                estadoPorteria = "Rechazado"
                                            )
                                            prefsManager.guardarSolicitudes(listaSolicitudes)
                                        }
                                    },
                                    onVerCalendarioClick = {
                                        pantallaAnterior = "home_coordinador"
                                        pantallaActual = "calendario"
                                    },
                                    onCerrarSesionClick = {
                                        realizarCerrarSesion()
                                    }
                                )
                            }

                            "home_entidad" -> {
                                val entidadActual = entidadLogueada
                                if (entidadActual != null) {
                                    EntidadHomeScreen(
                                        entidad = entidadActual,
                                        solicitudes = listaSolicitudes,
                                        onCambiarEstado = { id, nuevoEstado ->
                                            val index = listaSolicitudes.indexOfFirst { it.id == id }
                                            if (index != -1) {
                                                val item = listaSolicitudes[index]
                                                val itemActualizado = when (entidadActual) {
                                                    TipoEntidad.CTA -> item.copy(estadoCTA = nuevoEstado)
                                                    TipoEntidad.COMUNICACIONES -> item.copy(estadoComunicaciones = nuevoEstado)
                                                    TipoEntidad.SERVICIOS -> item.copy(estadoServiciosGenerales = nuevoEstado)
                                                    TipoEntidad.PORTERIA -> item.copy(estadoPorteria = nuevoEstado)
                                                }

                                                val nuevoEstadoGeneral = when {
                                                    nuevoEstado == "Rechazado" -> "Rechazado"
                                                    itemActualizado.estadoCTA == "Aprobado" &&
                                                            itemActualizado.estadoComunicaciones == "Aprobado" &&
                                                            itemActualizado.estadoServiciosGenerales == "Aprobado" &&
                                                            itemActualizado.estadoPorteria == "Aprobado" -> "Aprobado"
                                                    else -> itemActualizado.estadoGeneral
                                                }

                                                listaSolicitudes[index] = itemActualizado.copy(estadoGeneral = nuevoEstadoGeneral)
                                                prefsManager.guardarSolicitudes(listaSolicitudes)
                                            }
                                        },
                                        onCerrarSesionClick = {
                                            realizarCerrarSesion()
                                        }
                                    )
                                }
                            }

                            "detalle_solicitud" -> {
                                solicitudSeleccionada?.let { solicitud ->
                                    DetalleSolicitudScreen(
                                        solicitud = solicitud,
                                        onVolverClick = {
                                            pantallaActual = pantallaAnterior
                                        },
                                        onCambiarEstadoEntidad = { id, entidadStr, nuevoEstado ->
                                            val index = listaSolicitudes.indexOfFirst { it.id == id }
                                            if (index != -1) {
                                                val item = listaSolicitudes[index]
                                                val itemActualizado = when (entidadStr.lowercase()) {
                                                    "cta" -> item.copy(estadoCTA = nuevoEstado)
                                                    "comunicaciones" -> item.copy(estadoComunicaciones = nuevoEstado)
                                                    "servicios" -> item.copy(estadoServiciosGenerales = nuevoEstado)
                                                    "porteria" -> item.copy(estadoPorteria = nuevoEstado)
                                                    else -> item
                                                }

                                                val nuevoEstadoGeneral = when {
                                                    nuevoEstado == "Rechazado" -> "Rechazado"
                                                    itemActualizado.estadoCTA == "Aprobado" &&
                                                            itemActualizado.estadoComunicaciones == "Aprobado" &&
                                                            itemActualizado.estadoServiciosGenerales == "Aprobado" &&
                                                            itemActualizado.estadoPorteria == "Aprobado" -> "Aprobado"
                                                    else -> itemActualizado.estadoGeneral
                                                }

                                                val finalItem = itemActualizado.copy(estadoGeneral = nuevoEstadoGeneral)
                                                listaSolicitudes[index] = finalItem
                                                solicitudSeleccionada = finalItem
                                                prefsManager.guardarSolicitudes(listaSolicitudes)
                                            }
                                        }
                                    )
                                }
                            }

                            "solicitud" -> {
                                NuevaSolicitudScreen(
                                    onVolverClick = {
                                        pantallaActual = "home_profesor"
                                    },
                                    onVerCalendarioClick = {
                                        pantallaAnterior = "solicitud"
                                        pantallaActual = "calendario"
                                    },
                                    onSolicitudCreada = { nuevaSolicitud ->
                                        listaSolicitudes.add(nuevaSolicitud)
                                        prefsManager.guardarSolicitudes(listaSolicitudes)
                                        pantallaActual = "home_profesor"
                                    }
                                )
                            }

                            "calendario" -> {
                                CalendarioScreen(
                                    solicitudes = listaSolicitudes,
                                    onVolverClick = {
                                        pantallaActual = pantallaAnterior
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}