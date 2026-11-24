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
import kotlinx.coroutines.launch

// 1. Importación explícita del modelo centralizado
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

// 2. Importación directa de la persistencia local y nube
import cl.duoc.aplicacionmoviltherionslabs.data.PreferenciasManager
import cl.duoc.aplicacionmoviltherionslabs.data.RepositorioRecursos
import cl.duoc.aplicacionmoviltherionslabs.data.RepositorioSolicitudes

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
                    val coroutineScope = rememberCoroutineScope()

                    val loginViewModel: LoginViewModel = viewModel()

                    var pantallaActual by remember { mutableStateOf("login") }
                    var pantallaAnterior by remember { mutableStateOf("home_profesor") }
                    var solicitudSeleccionada by remember { mutableStateOf<SolicitudActividad?>(null) }
                    var entidadLogueada by remember { mutableStateOf<TipoEntidad?>(null) }

                    var rolActual by remember { mutableStateOf<String?>(null) }
                    var solicitudPendienteNotificacion by remember { mutableStateOf<String?>(null) }

                    val listaSolicitudes = remember {
                        mutableStateListOf<SolicitudActividad>().apply {
                            addAll(prefsManager.cargarSolicitudes())
                        }
                    }

                    // Sincronización inicial con Supabase (GET recursos y GET solicitudes)
                    LaunchedEffect(Unit) {
                        RepositorioRecursos.sincronizarRecursosDesdeNube()
                        val remotas = RepositorioSolicitudes.obtenerSolicitudesNube()
                        if (remotas.isNotEmpty()) {
                            listaSolicitudes.clear()
                            listaSolicitudes.addAll(remotas)
                            prefsManager.guardarSolicitudes(listaSolicitudes)
                        }
                    }

                    val realizarCerrarSesion = {
                        loginViewModel.cerrarSesion()
                        solicitudSeleccionada = null
                        entidadLogueada = null
                        rolActual = null
                        solicitudPendienteNotificacion = null
                        pantallaActual = "login"
                    }

                    val intentObservado by intentActualState
                    LaunchedEffect(intentObservado) {
                        val destino = intentObservado?.getStringExtra("PANTALLA_DESTINO")
                        val idSolicitud = intentObservado?.getStringExtra("SOLICITUD_ID")

                        if (destino == "detalle_solicitud" && !idSolicitud.isNullOrBlank()) {
                            intentObservado?.removeExtra("PANTALLA_DESTINO")
                            intentObservado?.removeExtra("SOLICITUD_ID")

                            if (rolActual == null) {
                                solicitudPendienteNotificacion = idSolicitud
                                pantallaActual = "login"
                            } else {
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

                    AnimatedContent(
                        targetState = pantallaActual,
                        transitionSpec = {
                            val esSubpantalla = targetState in listOf("calendario", "detalle_solicitud", "solicitud")
                            val vieneDeSubpantalla = initialState in listOf("calendario", "detalle_solicitud", "solicitud")

                            when {
                                initialState == "login" -> {
                                    (fadeIn(animationSpec = tween(350)) + scaleIn(initialScale = 0.96f, animationSpec = tween(350)))
                                        .togetherWith(fadeOut(animationSpec = tween(250)))
                                }
                                targetState == "login" -> {
                                    fadeIn(animationSpec = tween(300))
                                        .togetherWith(fadeOut(animationSpec = tween(300)))
                                }
                                esSubpantalla -> {
                                    (slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeIn(tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth / 3 } + fadeOut(tween(250)))
                                }
                                vieneDeSubpantalla -> {
                                    (slideInHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth / 3 } + fadeIn(tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeOut(tween(250)))
                                }
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
                                        // Refrescamos solicitudes desde Supabase al iniciar sesión
                                        coroutineScope.launch {
                                            val remotas = RepositorioSolicitudes.obtenerSolicitudesNube()
                                            if (remotas.isNotEmpty()) {
                                                listaSolicitudes.clear()
                                                listaSolicitudes.addAll(remotas)
                                                prefsManager.guardarSolicitudes(listaSolicitudes)
                                            }
                                        }

                                        val correoLibreria = correo.lowercase().trim()
                                        val entidad = TipoEntidad.desdeCorreo(correoLibreria)

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
                                            val aprobada = item.copy(
                                                estadoGeneral = "Aprobado",
                                                estadoComunicaciones = "Aprobado",
                                                estadoServiciosGenerales = "Aprobado",
                                                estadoCTA = "Aprobado",
                                                estadoPorteria = "Aprobado"
                                            )
                                            listaSolicitudes[index] = aprobada
                                            prefsManager.guardarSolicitudes(listaSolicitudes)
                                            // PUT en Supabase
                                            coroutineScope.launch {
                                                RepositorioSolicitudes.actualizarEstadoSolicitudNube(aprobada)
                                            }
                                        }
                                    },
                                    onRechazarSolicitud = { id ->
                                        val index = listaSolicitudes.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            val item = listaSolicitudes[index]
                                            val rechazada = item.copy(
                                                estadoGeneral = "Rechazado",
                                                estadoComunicaciones = "Rechazado",
                                                estadoServiciosGenerales = "Rechazado",
                                                estadoCTA = "Rechazado",
                                                estadoPorteria = "Rechazado"
                                            )
                                            listaSolicitudes[index] = rechazada
                                            prefsManager.guardarSolicitudes(listaSolicitudes)
                                            // PUT en Supabase
                                            coroutineScope.launch {
                                                RepositorioSolicitudes.actualizarEstadoSolicitudNube(rechazada)
                                            }
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

                                                val finalItem = itemActualizado.copy(estadoGeneral = nuevoEstadoGeneral)
                                                listaSolicitudes[index] = finalItem
                                                prefsManager.guardarSolicitudes(listaSolicitudes)
                                                // PUT en Supabase
                                                coroutineScope.launch {
                                                    RepositorioSolicitudes.actualizarEstadoSolicitudNube(finalItem)
                                                }
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
                                                // PUT en Supabase
                                                coroutineScope.launch {
                                                    RepositorioSolicitudes.actualizarEstadoSolicitudNube(finalItem)
                                                }
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
                                        // POST en Supabase
                                        coroutineScope.launch {
                                            RepositorioSolicitudes.guardarSolicitudNube(nuevaSolicitud)
                                        }
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