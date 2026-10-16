package cl.duoc.aplicacionmoviltherionslabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
                    color = Color(0xFFF8F9FA) // Fondo blanco/gris ultra limpio
                ) {
                    var pantallaActual by remember { mutableStateOf("login") }
                    var pantallaAnterior by remember { mutableStateOf("home_profesor") }

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
                                onVerCalendarioClick = {
                                    pantallaAnterior = "home_coordinador"
                                    pantallaActual = "calendario"
                                },
                                onCerrarSesionClick = {
                                    pantallaActual = "login"
                                }
                            )
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