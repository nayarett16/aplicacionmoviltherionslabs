package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Imports corregidos a TU paquete exacto
import cl.duoc.aplicacionmoviltherionslabs.model.EstadoReserva
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudReserva
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorHomeScreen(
    onBuscarSalasClick: () -> Unit = {},
    onNuevaSolicitudClick: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    val misReservas = remember {
        listOf(
            SolicitudReserva("1", "LC25", "Prof. R. Morales", "Desarrollo Mobile", "2026-10-15", "08:30", "10:00", EstadoReserva.APROBADA),
            SolicitudReserva("2", "AUD01", "Prof. R. Morales", "Taller de Integración", "2026-10-18", "11:00", "12:30", EstadoReserva.PENDIENTE)
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulDuoc)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "¡HOLA, DOCENTE!",
                            color = AmarilloDuoc,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel de profesor",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onCerrarSesionClick) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaSolicitudClick,
                containerColor = AzulDuoc,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva Solicitud") },
                text = { Text("Solicitar Sala", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrisFondoDuoc)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "ACCIONES RÁPIDAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AzulDuoc
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccionCard(
                    titulo = "Ver Disponibilidad",
                    subtitulo = "Explorar salas disponibles",
                    modifier = Modifier.weight(1f),
                    onClick = onBuscarSalasClick
                )

                AccionCard(
                    titulo = "Mis Horarios",
                    subtitulo = "Clases de la semana",
                    modifier = Modifier.weight(1f),
                    onClick = { /* Próximamente */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MIS RESERVAS RECIENTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AzulDuoc
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(misReservas) { reserva ->
                    MiReservaCard(solicitud = reserva)
                }
            }
        }
    }
}

@Composable
fun AccionCard(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AzulDuoc
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitulo,
                fontSize = 11.sp,
                color = TextoGrisDuoc
            )
        }
    }
}

@Composable
fun MiReservaCard(solicitud: SolicitudReserva) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sala ${solicitud.salaCodigo} - ${solicitud.asignatura}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${solicitud.fecha} | ${solicitud.horaInicio} - ${solicitud.horaFin}",
                    fontSize = 12.sp,
                    color = TextoGrisDuoc
                )
            }

            val (colorFondo, colorTexto, texto) = when (solicitud.estado) {
                EstadoReserva.PENDIENTE -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "Pendiente")
                EstadoReserva.APROBADA -> Triple(FondoDisponible, DisponibleVerde, "Aprobada")
                EstadoReserva.RECHAZADA -> Triple(FondoReservado, DuocErrorRed, "Rechazada")
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colorFondo
            ) {
                Text(
                    text = texto,
                    color = colorTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}