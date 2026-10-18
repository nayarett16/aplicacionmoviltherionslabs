package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSolicitudScreen(
    solicitud: SolicitudActividad,
    onVolverClick: () -> Unit = {},
    onCambiarEstadoEntidad: (solicitudId: String, entidad: String, nuevoEstado: String) -> Unit = { _, _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de Solicitud",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onVolverClick) {
                        Text(
                            text = "← Volver",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta con Información de la Actividad
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = solicitud.nombreActividad,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002A5C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "👤 Responsable: ${solicitud.responsable}", fontSize = 14.sp)
                        Text(text = "🏫 Escuela/Área: ${solicitud.escuelaArea}", fontSize = 14.sp)
                        Text(text = "📅 Fecha: ${solicitud.fechaTexto} - ⏰ Hora: ${solicitud.horaTexto}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                        if (solicitud.recursosSeleccionados.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Recursos Solicitados:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002A5C),
                                fontSize = 14.sp
                            )
                            solicitud.recursosSeleccionados.forEach { recurso ->
                                Text(text = "• $recurso", fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Aprobación por Entidad (5 Áreas)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002A5C)
                )
            }

            // Las 5 Entidades Institucionales
            val entidades = listOf(
                Triple("CTA / Digitales", solicitud.estadoCTA, "cta"),
                Triple("Comunicaciones", solicitud.estadoComunicaciones, "comunicaciones"),
                Triple("Servicios Generales", solicitud.estadoServiciosGenerales, "servicios"),
                Triple("Portería", solicitud.estadoPorteria, "porteria"),
                Triple("Extensión (Jefatura)", solicitud.estadoGeneral, "extension")
            )

            items(entidades.size) { index ->
                val (nombreEntidad, estadoActual, claveEntidad) = entidades[index]

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nombreEntidad,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF002A5C)
                            )
                            Text(
                                text = "Estado: $estadoActual",
                                fontSize = 12.sp,
                                color = when (estadoActual) {
                                    "Aprobado" -> Color(0xFF2E7D32)
                                    "Rechazado" -> Color(0xFFC62828)
                                    else -> Color(0xFFF57C00)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { onCambiarEstadoEntidad(solicitud.id, claveEntidad, "Aprobado") }
                            ) {
                                Text("✅", fontSize = 18.sp)
                            }
                            IconButton(
                                onClick = { onCambiarEstadoEntidad(solicitud.id, claveEntidad, "Rechazado") }
                            ) {
                                Text("❌", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}