package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    solicitudes: List<SolicitudActividad> = emptyList(),
    onVolverClick: () -> Unit = {}
) {
    // 1. Obtiene automáticamente el día actual del sistema
    val hoy = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }
    var diaSeleccionado by remember { mutableStateOf(hoy) }

    val diasDelMes = (1..31).toList()
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    // 2. Calcula el desfasaje exacto de Octubre 2026 (El 1 es Jueves = 3 espacios vacíos previos)
    val primerDiaOffset = remember {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 1)
        }
        val diaSemanaIndex = cal.get(Calendar.DAY_OF_WEEK) // 1=Dom, 2=Lun... 5=Jue
        if (diaSemanaIndex == Calendar.SUNDAY) 6 else diaSemanaIndex - 2
    }

    // Feriados oficiales de Octubre en Chile (12 y 31)
    val feriadosOctubre = remember { listOf(12, 31) }

    val diaFormateado = String.format("%02d", diaSeleccionado)

    val actividadesDelDia = solicitudes.filter { solicitud ->
        val partesFecha = solicitud.fechaTexto.split("/")
        if (partesFecha.isNotEmpty()) {
            partesFecha[0] == diaFormateado
        } else false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda Nativa de Sede",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Octubre 2026",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF002A5C)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        diasSemana.forEach { diaNombre ->
                            Text(
                                text = diaNombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF002A5C),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(230.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Relleno de casillas vacías antes del día 1
                        items(primerDiaOffset) {
                            Box(modifier = Modifier.size(36.dp))
                        }

                        // Renderizado correcto de los días del mes
                        items(diasDelMes) { diaNumero ->
                            val columnaActual = (primerDiaOffset + diaNumero - 1) % 7
                            val esDomingo = (columnaActual == 6)
                            val esFeriado = feriadosOctubre.contains(diaNumero)
                            val esInvalido = esDomingo || esFeriado
                            val esSeleccionado = (diaNumero == diaSeleccionado)

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            esSeleccionado -> Color(0xFFFFC107)
                                            esInvalido -> Color(0xFFFFEBEE)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        diaSeleccionado = diaNumero
                                    }
                            ) {
                                Text(
                                    text = diaNumero.toString(),
                                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        esInvalido -> Color(0xFFC62828)
                                        esSeleccionado -> Color(0xFF002A5C)
                                        else -> Color.DarkGray
                                    },
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Actividades del día $diaSeleccionado de Octubre",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002A5C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val columnaSeleccionada = (primerDiaOffset + diaSeleccionado - 1) % 7
            val esDomingoSeleccionado = (columnaSeleccionada == 6)
            val esFeriadoSeleccionado = feriadosOctubre.contains(diaSeleccionado)

            if (esDomingoSeleccionado || esFeriadoSeleccionado) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (esDomingoSeleccionado)
                                "🚫 La sede permanece cerrada los días domingo."
                            else
                                "🎉 Día feriado institucional. No hay atención.",
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (actividadesDelDia.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📅 No hay actividades agendadas para este día.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(actividadesDelDia) { evento ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
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
                                Column {
                                    Text(
                                        text = evento.nombreActividad,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF002A5C)
                                    )
                                    Text(
                                        text = "Responsable: ${evento.responsable}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "📅 ${evento.fechaTexto} - ⏰ ${evento.horaTexto}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                Surface(
                                    color = Color(0xFFFFE082),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = evento.estadoGeneral,
                                        color = Color(0xFF5D4037),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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