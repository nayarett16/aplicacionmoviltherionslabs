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

data class SolicitudExtension(
    val id: String,
    val actividad: String,
    val solicitante: String,
    val fechaHora: String,
    var estadoComunicaciones: String = "Pendiente",
    var estadoServiciosGenerales: String = "Pendiente",
    var estadoCTA: String = "Pendiente",
    var estadoPorteria: String = "Pendiente"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinadorHomeScreen(
    onVerCalendarioClick: () -> Unit = {}, // <-- AGREGADO: Parámetro para navegar al calendario
    onCerrarSesionClick: () -> Unit = {}
) {
    var solicitudes by remember {
        mutableStateOf(
            listOf(
                SolicitudExtension(
                    id = "101",
                    actividad = "Festival de Canto DUOC",
                    solicitante = "Prof. Ana María",
                    fechaHora = "28 Oct 2026 - Bloque 2 (10:00 - 11:20)",
                    estadoComunicaciones = "Aprobado",
                    estadoServiciosGenerales = "Pendiente",
                    estadoCTA = "Aprobado",
                    estadoPorteria = "Pendiente"
                ),
                SolicitudExtension(
                    id = "102",
                    actividad = "Feria Tecnológica Therions",
                    solicitante = "Estudiante Carlos Pérez",
                    fechaHora = "02 Nov 2026 - Bloque 4 (13:00 - 14:20)",
                    estadoComunicaciones = "Pendiente",
                    estadoServiciosGenerales = "Pendiente",
                    estadoCTA = "Pendiente",
                    estadoPorteria = "Aprobado"
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel Extensión (Super Admin)",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                actions = {
                    TextButton(onClick = onCerrarSesionClick) {
                        Text("Cerrar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            // AGREGADO: Botón para acceder al Calendario Nativo desde el panel del Coordinador
            OutlinedButton(
                onClick = onVerCalendarioClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "📅 Ver Agenda de Actividades de la Sede",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Validación Global de Eventos y Recursos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(solicitudes) { sol ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = sol.actividad,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Solicita: ${sol.solicitante}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Fecha/Hora: ${sol.fechaHora}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(
                                text = "Estado por Jefaturas de Área:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Estado por Áreas
                            AreaStatusRow("Comunicaciones", sol.estadoComunicaciones)
                            AreaStatusRow("Servicios Generales", sol.estadoServiciosGenerales)
                            AreaStatusRow("CTA / Digitales", sol.estadoCTA)
                            AreaStatusRow("Portería", sol.estadoPorteria)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Botón de Validación Super-Dios
                            Button(
                                onClick = {
                                    // Forzar aprobación completa desde Extensión
                                    solicitudes = solicitudes.map {
                                        if (it.id == sol.id) {
                                            it.copy(
                                                estadoComunicaciones = "Aprobado",
                                                estadoServiciosGenerales = "Aprobado",
                                                estadoCTA = "Aprobado",
                                                estadoPorteria = "Aprobado"
                                            )
                                        } else it
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Aprobar Todo (Validación Extensión)", color = Color(0xFFFFC107))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AreaStatusRow(area: String, estado: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = area, fontSize = 13.sp)
        Text(
            text = estado,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when (estado) {
                "Aprobado" -> Color(0xFF2E7D32)
                "Rechazado" -> Color(0xFFC62828)
                else -> Color(0xFFF57F17)
            }
        )
    }
}