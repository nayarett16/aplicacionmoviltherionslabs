package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorHomeScreen(
    solicitudes: List<SolicitudActividad>,
    onNuevaSolicitudClick: () -> Unit,
    onVerCalendarioClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    // Estado para la micro-interacción táctil del botón principal
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val escalaBoton by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "EscalaBotonSolicitud"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel Comunidad",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    TextButton(onClick = { mostrarDialogoCerrarSesion = true }) {
                        Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF002A5C))
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
            // Tarjeta de Cabecera: Información Docente + Acceso a la Agenda General
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Docente / Comunidad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF002A5C)
                    )
                    Text(
                        text = "Sede Duoc UC",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onVerCalendarioClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📅 Ver Agenda e Historial de Sede",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002A5C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNuevaSolicitudClick,
                interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Color(0xFF002A5C)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .graphicsLayer {
                        scaleX = escalaBoton
                        scaleY = escalaBoton
                    }
            ) {
                Text("➕ Crear Nueva Solicitud de Evento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mis Solicitudes Realizadas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002A5C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = solicitudes.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no has registrado ninguna solicitud.", color = Color.Gray, fontSize = 14.sp)
                }
            }

            AnimatedVisibility(
                visible = solicitudes.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(500)) + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(solicitudes, key = { it.id }) { solicitud ->
                        var expandida by remember { mutableStateOf(false) }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                .clickable { expandida = !expandida }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
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

                                    AnimatedContent(
                                        targetState = solicitud.estadoGeneral,
                                        label = "EstadoAnimation"
                                    ) { estado ->
                                        Surface(
                                            color = when (estado) {
                                                "Aprobado" -> Color(0xFFE8F5E9)
                                                "Rechazado" -> Color(0xFFFFEBEE)
                                                else -> Color(0xFFFFF8E1)
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = estado.uppercase(),
                                                color = when (estado) {
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
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📅 ${solicitud.fechaTexto}  |  ⏰ ${solicitud.horaTexto}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )

                                if (solicitud.recursosSeleccionados.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    solicitud.recursosSeleccionados.forEach { recurso ->
                                        Text(
                                            text = "• $recurso",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                }

                                // Detalle desplegable por área al tocar la tarjeta
                                if (expandida) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFFEEEEEE))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Estado por Departamento:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF002A5C)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("• CTA: ${solicitud.estadoCTA}", fontSize = 11.sp, color = Color.DarkGray)
                                    Text("• Comunicaciones: ${solicitud.estadoComunicaciones}", fontSize = 11.sp, color = Color.DarkGray)
                                    Text("• Servicios Generales: ${solicitud.estadoServiciosGenerales}", fontSize = 11.sp, color = Color.DarkGray)
                                    Text("• Portería: ${solicitud.estadoPorteria}", fontSize = 11.sp, color = Color.DarkGray)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (expandida) "▲ Ocultar detalle por área" else "▼ Ver estado por área",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para Cerrar Sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = {
                Text(text = "Cerrar Sesión", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "¿Está seguro que desea cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCerrarSesionClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text(text = "Sí, cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = false }
                ) {
                    Text(text = "Cancelar", color = Color(0xFF002A5C))
                }
            }
        )
    }
}