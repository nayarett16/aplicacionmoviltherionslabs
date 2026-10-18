package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

enum class TipoEntidad(val nombreArea: String, val palabrasClave: List<String>) {
    CTA("CTA / Digitales", listOf("Micrófono", "Proyector")),
    COMUNICACIONES("Comunicaciones", listOf("Fotográfica", "Cobertura")),
    SERVICIOS("Servicios Generales", listOf("Aseo", "Camarines")),
    PORTERIA("Portería", listOf("Acceso", "Control"));

    companion object {
        fun desdeCorreo(correo: String): TipoEntidad? {
            val c = correo.lowercase().trim()
            return when {
                c.contains("cta") -> CTA
                c.contains("comunicaciones") || c.contains("comunicacion") -> COMUNICACIONES
                c.contains("servicios") || c.contains("mantencion") -> SERVICIOS
                c.contains("porteria") || c.contains("seguridad") -> PORTERIA
                else -> null
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntidadHomeScreen(
    entidad: TipoEntidad,
    solicitudes: List<SolicitudActividad> = emptyList(),
    onCambiarEstado: (solicitudId: String, nuevoEstado: String) -> Unit = { _, _ -> },
    onCerrarSesionClick: () -> Unit = {}
) {
    // Filtrar solo las solicitudes que requieren recursos de esta área específica
    val solicitudesFiltradas = solicitudes.filter { solicitud ->
        solicitud.recursosSeleccionados.any { recurso ->
            entidad.palabrasClave.any { palabra -> recurso.contains(palabra, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel ${entidad.nombreArea}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onCerrarSesionClick) {
                        Text(
                            text = "Cerrar Sesión",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF002A5C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            Text(
                text = "Solicitudes Asignadas a Tu Área",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002A5C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (solicitudes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay solicitudes pendientes para gestionar.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(solicitudes, key = { it.id }) { solicitud ->
                        val estadoActual = when (entidad) {
                            TipoEntidad.CTA -> solicitud.estadoCTA
                            TipoEntidad.COMUNICACIONES -> solicitud.estadoComunicaciones
                            TipoEntidad.SERVICIOS -> solicitud.estadoServiciosGenerales
                            TipoEntidad.PORTERIA -> solicitud.estadoPorteria
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = solicitud.nombreActividad,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF002A5C)
                                    )

                                    Surface(
                                        color = when (estadoActual) {
                                            "Aprobado" -> Color(0xFFE8F5E9)
                                            "Rechazado" -> Color(0xFFFFEBEE)
                                            else -> Color(0xFFFFF8E1)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = estadoActual.uppercase(),
                                            color = when (estadoActual) {
                                                "Aprobado" -> Color(0xFF2E7D32)
                                                "Rechazado" -> Color(0xFFC62828)
                                                else -> Color(0xFFF57F17)
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "👤 Responsable: ${solicitud.responsable} (${solicitud.escuelaArea})",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = "📅 Fecha: ${solicitud.fechaTexto} | ⏰ Hora: ${solicitud.horaTexto}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )

                                if (solicitud.recursosSeleccionados.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Recursos: ${solicitud.recursosSeleccionados.joinToString(", ")}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onCambiarEstado(solicitud.id, "Aprobado") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Aprobar", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { onCambiarEstado(solicitud.id, "Rechazado") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Rechazar", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
