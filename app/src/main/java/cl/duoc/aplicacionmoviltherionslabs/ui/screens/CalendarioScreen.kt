package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    solicitudes: List<SolicitudActividad> = emptyList(),
    onVolverClick: () -> Unit = {}
) {
    val datePickerState = rememberDatePickerState()

    val fechaSeleccionadaTexto = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date(millis))
        } ?: ""
    }

    val solicitudesDelDia = remember(solicitudes, fechaSeleccionadaTexto) {
        if (fechaSeleccionadaTexto.isBlank()) {
            emptyList()
        } else {
            solicitudes.filter { it.fechaTexto == fechaSeleccionadaTexto }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda e Historial de Sede",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onVolverClick) {
                        Text("← Volver", color = Color.White, fontWeight = FontWeight.Bold)
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
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DatePicker(
                    state = datePickerState,
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animación suave en el título al cambiar de día
            AnimatedContent(
                targetState = fechaSeleccionadaTexto,
                transitionSpec = {
                    (fadeIn(tween(250)) + slideInVertically { -it / 2 })
                        .togetherWith(fadeOut(tween(200)) + slideOutVertically { it / 2 })
                },
                label = "TituloFechaAnimado"
            ) { fecha ->
                Text(
                    text = if (fecha.isNotBlank())
                        "Actividades para el $fecha"
                    else
                        "Selecciona una fecha en el calendario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002A5C),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Animación al filtrar eventos por día o mostrar mensajes vacíos
            AnimatedContent(
                targetState = Pair(fechaSeleccionadaTexto, solicitudesDelDia),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 6 })
                        .togetherWith(fadeOut(animationSpec = tween(200)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "ListaEventosDiaAnimada"
            ) { (fecha, eventos) ->
                when {
                    fecha.isBlank() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Haz clic en un día del calendario para revisar sus eventos.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    eventos.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay eventos creados en la agenda para este día.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = eventos,
                                key = { it.id }
                            ) { solicitud ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
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
                                                color = when (solicitud.estadoGeneral) {
                                                    "Aprobado" -> Color(0xFFE8F5E9)
                                                    "Rechazado" -> Color(0xFFFFEBEE)
                                                    else -> Color(0xFFFFF8E1)
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = solicitud.estadoGeneral.uppercase(),
                                                    color = when (solicitud.estadoGeneral) {
                                                        "Aprobado" -> Color(0xFF2E7D32)
                                                        "Rechazado" -> Color(0xFFC62828)
                                                        else -> Color(0xFFF57F17)
                                                    },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "⏰ Hora: ${solicitud.horaTexto}  |  🏫 ${solicitud.escuelaArea}",
                                            color = Color.DarkGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        if (solicitud.responsable.isNotBlank()) {
                                            Text(
                                                text = "👤 Responsable: ${solicitud.responsable}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
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
}