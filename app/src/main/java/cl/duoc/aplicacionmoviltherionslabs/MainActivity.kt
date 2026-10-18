package cl.duoc.aplicacionmoviltherionslabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1. Importación explícita del modelo desde el paquete model
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

// 2. Importación directa de la persistencia
import cl.duoc.aplicacionmoviltherionslabs.data.PreferenciasManager

// 3. Importaciones explícitas de las pantallas
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.LoginScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.ProfesorHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.CoordinadorHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.EntidadHomeScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.DetalleSolicitudScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.NuevaSolicitudScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.CalendarioScreen
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.TipoEntidad

import cl.duoc.aplicacionmoviltherionslabs.ui.theme.AplicacionMovilTherionsLabsTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionMovilTherionsLabsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    val context = LocalContext.current
                    val prefsManager = remember { PreferenciasManager(context) }

                    var pantallaActual by remember { mutableStateOf("login") }
                    var pantallaAnterior by remember { mutableStateOf("home_profesor") }
                    var solicitudSeleccionada by remember { mutableStateOf<SolicitudActividad?>(null) }
                    var entidadLogueada by remember { mutableStateOf<TipoEntidad?>(null) }

                    // Carga inicial de datos desde SharedPreferences (Persistencia Local - IE 2.3.1)
                    val listaSolicitudes = remember {
                        mutableStateListOf<SolicitudActividad>().apply {
                            addAll(prefsManager.cargarSolicitudes())
                        }
                    }

                    when (pantallaActual) {
                        "login" -> {
                            LoginScreen(
                                onLoginSuccess = { correo ->
                                    val correoLibrería = correo.lowercase().trim()
                                    val entidad = TipoEntidad.desdeCorreo(correoLibrería)

                                    when {
                                        correoLibrería.contains("extension") ||
                                                correoLibrería.contains("coordinacion") ||
                                                correoLibrería.contains("coordinador") -> {
                                            pantallaActual = "home_coordinador"
                                        }
                                        entidad != null -> {
                                            entidadLogueada = entidad
                                            pantallaActual = "home_entidad"
                                        }
                                        else -> {
                                            // Por defecto: Comunidad / Docentes
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
                                    pantallaActual = "login"
                                }
                            )
                        }

                        "home_coordinador" -> {
                            CoordinadorHomeScreen(
                                solicitudes = listaSolicitudes,
                                onSolicitudClick = { solicitud ->
                                    solicitudSeleccionada = solicitud
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
                                    pantallaActual = "login"
                                }
                            )
                        }

                        "home_entidad" -> {
                            entidadLogueada?.let { entidad ->
                                EntidadHomeScreen(
                                    entidad = entidad,
                                    solicitudes = listaSolicitudes,
                                    onCambiarEstado = { id, nuevoEstado ->
                                        val index = listaSolicitudes.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            val item = listaSolicitudes[index]
                                            val itemActualizado = when (entidad) {
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
                                        pantallaActual = "login"
                                    }
                                )
                            }
                        }

                        "detalle_solicitud" -> {
                            solicitudSeleccionada?.let { solicitud ->
                                DetalleSolicitudScreen(
                                    solicitud = solicitud,
                                    onVolverClick = {
                                        pantallaActual = "home_coordinador"
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