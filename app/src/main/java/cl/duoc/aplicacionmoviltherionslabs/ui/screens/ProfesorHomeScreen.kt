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

// Modelo de datos compartido para las solicitudes
data class SolicitudActividad(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombreActividad: String = "",
    val responsable: String = "",
    val fechaTexto: String = "",
    val horaTexto: String = "",
    val lugar: String = "",
    val descripcion: String = "",
    val estadoGeneral: String = "Pendiente",
    val estadoCTA: String = "Pendiente",
    val estadoComunicaciones: String = "Pendiente",
    val estadoServiciosGenerales: String = "Pendiente",
    val estadoPorteria: String = "Pendiente",
    val recursosSeleccionados: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorHomeScreen(
    solicitudes: List<SolicitudActividad> = emptyList(),
    onBuscarSalasClick: () -> Unit = {},
    onNuevaSolicitudClick: () -> Unit = {},
    onVerCalendarioClick: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Portal Docente",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onCerrarSesionClick) {
                        Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
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
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNuevaSolicitudClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107),
                        contentColor = Color(0xFF002A5C)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("➕ Nueva Solicitud", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onVerCalendarioClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF002A5C)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("📅 Ver Agenda", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Mis Solicitudes Enviadas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002A5C),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (solicitudes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no has creado ninguna solicitud.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Sintaxis correcta pasando la lista directamente
                    items(solicitudes) { solicitud ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
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
                                        color = Color(0xFFFFE082),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = solicitud.estadoGeneral,
                                            color = Color(0xFF5D4037),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📅 Fecha: ${solicitud.fechaTexto}\n⏰ Hora: ${solicitud.horaTexto}",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp
                                )

                                if (solicitud.recursosSeleccionados.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Recursos Pedidos:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF002A5C)
                                    )
                                    solicitud.recursosSeleccionados.forEach { recurso ->
                                        Text(
                                            text = "• $recurso",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(start = 6.dp)
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
}