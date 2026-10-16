package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    solicitudes: List<SolicitudActividad> = emptyList(),
    onVolverClick: () -> Unit = {}
) {
    // Filtro por defecto: Mostrar solo solicitudes aprobadas en la agenda institucional
    var mostrarSoloAprobadas by remember { mutableStateOf(true) }

    val solicitudesFiltradas = if (mostrarSoloAprobadas) {
        solicitudes.filter { it.estadoGeneral == "Aprobado" }
    } else {
        solicitudes
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda General de Sede",
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
                    containerColor = Color(0xFF002A5C) // AZUL INSTITUCIONAL
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Control de Filtro (Chip Aprobados vs Todos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Eventos Programados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002A5C)
                )

                FilterChip(
                    selected = mostrarSoloAprobadas,
                    onClick = { mostrarSoloAprobadas = !mostrarSoloAprobadas },
                    label = {
                        Text(
                            text = if (mostrarSoloAprobadas) "Solo Confirmados ✅" else "Todos los Estados 📋",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE8F5E9),
                        selectedLabelColor = Color(0xFF2E7D32)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (solicitudesFiltradas.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (mostrarSoloAprobadas)
                                "No hay eventos confirmados en la agenda."
                            else
                                "No hay registros de solicitudes.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(solicitudesFiltradas) { solicitud ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (solicitud.estadoGeneral == "Aprobado") Color(0xFFF1F8E9) else Color(0xFFFFFDE7)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = solicitud.nombreActividad,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF002A5C)
                                    )

                                    Surface(
                                        color = when (solicitud.estadoGeneral) {
                                            "Aprobado" -> Color(0xFF2E7D32)
                                            "Rechazado" -> Color(0xFFC62828)
                                            else -> Color(0xFFFFC107)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = solicitud.estadoGeneral,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📅 Fecha: ${solicitud.fechaTexto} - ⏰ Hora: ${solicitud.horaTexto}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = "🏫 Área: ${solicitud.escuelaArea} | Responsable: ${solicitud.responsable}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                if (solicitud.recursosSeleccionados.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Recursos reservados: ${solicitud.recursosSeleccionados.joinToString(", ")}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF002A5C),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}