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
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import cl.duoc.aplicacionmoviltherionslabs.ui.screens.*
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
                    var pantallaActual by remember { mutableStateOf("login") }
                    var pantallaAnterior by remember { mutableStateOf("home_profesor") }
                    var solicitudSeleccionada by remember { mutableStateOf<SolicitudActividad?>(null) }

                    val listaSolicitudes = remember { mutableStateListOf<SolicitudActividad>() }

                    when (pantallaActual) {
                        "login" -> {
                            LoginScreen(
                                onLoginSuccess = { correo ->
                                    if (correo.contains("coordinacion", ignoreCase = true) ||
                                        correo.contains("coordinador", ignoreCase = true)) {
                                        pantallaActual = "home_coordinador"
                                    } else {
                                        pantallaActual = "home_profesor"
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
                                            estadoComunicaciones = "Aprobado",
                                            estadoServiciosGenerales = "Aprobado",
                                            estadoCTA = "Aprobado",
                                            estadoPorteria = "Aprobado"
                                        )
                                    }
                                },
                                onRechazarSolicitud = { id ->
                                    val index = listaSolicitudes.indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        val item = listaSolicitudes[index]
                                        listaSolicitudes[index] = item.copy(
                                            estadoComunicaciones = "Rechazado",
                                            estadoServiciosGenerales = "Rechazado",
                                            estadoCTA = "Rechazado",
                                            estadoPorteria = "Rechazado"
                                        )
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

                        "detalle_solicitud" -> {
                            solicitudSeleccionada?.let { solicitud ->
                                DetalleSolicitudScreen(
                                    solicitud = solicitud,
                                    onVolverClick = {
                                        pantallaActual = "home_coordinador"
                                    },
                                    onCambiarEstadoEntidad = { id, entidad, nuevoEstado ->
                                        val index = listaSolicitudes.indexOfFirst { it.id == id }
                                        if (index != -1) {
                                            val item = listaSolicitudes[index]
                                            val itemActualizado = when (entidad) {
                                                "cta" -> item.copy(estadoCTA = nuevoEstado)
                                                "comunicaciones" -> item.copy(estadoComunicaciones = nuevoEstado)
                                                "servicios" -> item.copy(estadoServiciosGenerales = nuevoEstado)
                                                "porteria" -> item.copy(estadoPorteria = nuevoEstado)
                                                else -> item
                                            }
                                            listaSolicitudes[index] = itemActualizado
                                            solicitudSeleccionada = itemActualizado
                                        }
                                    }
                                )
                            }
                        }

                        "espacios" -> {
                            EspaciosScreen(
                                onVolverClick = {
                                    pantallaActual = "home_profesor"
                                }
                            )
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