package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import cl.duoc.aplicacionmoviltherionslabs.data.RepositorioRecursos
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinadorHomeScreen(
    solicitudes: List<SolicitudActividad> = emptyList(),
    onSolicitudClick: (SolicitudActividad) -> Unit = {},
    onAprobarSolicitud: (String) -> Unit = {},
    onRechazarSolicitud: (String) -> Unit = {},
    onVerCalendarioClick: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var mensajeAlerta by remember { mutableStateOf<String?>(null) }

    val todosLosRecursos = remember { RepositorioRecursos.obtenerTodosLosRecursos() }

    val mapaStockGlobal = remember(solicitudes, todosLosRecursos) {
        todosLosRecursos.associate { recurso ->
            val cantidadUsada = solicitudes.count { solicitud ->
                solicitud.estadoGeneral == "Aprobado" &&
                        solicitud.recursosSeleccionados.any { it.contains(recurso.nombre, ignoreCase = true) }
            }
            val stockRestante = (recurso.stockDisponible - cantidadUsada).coerceAtLeast(0)
            recurso.id to stockRestante
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel Extensión / Coordinación",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = { mostrarDialogoCerrarSesion = true }) {
                        Text(
                            text = "Cerrar Sesión",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF002A5C))
            )
        }
    ) { paddingValues ->
        // UN SOLO LAZYCOLUMN PARA QUE TODA LA PANTALLA HAGA SCROLL JUNTA
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ITEM 1: GUINCHA DE INVENTARIO
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF002A5C)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📦 GUINCHA DE INVENTARIO GENERAL (TODAS LAS ÁREAS)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val gruposPorArea = todosLosRecursos.groupBy { it.area }
                        gruposPorArea.forEach { (area, recursos) ->
                            Text(
                                text = "• $area:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(recursos) { recurso ->
                                    val stock = mapaStockGlobal[recurso.id] ?: 0
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (stock > 0) Color(0xFF1E3A8A) else Color(0xFF7F1D1D)
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.width(110.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = recurso.nombre,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Stock: $stock",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (stock > 0) Color(0xFF4ADE80) else Color(0xFFFCA5A5)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ITEM 2: BOTÓN DE AGENDA
            item {
                Button(
                    onClick = onVerCalendarioClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107),
                        contentColor = Color(0xFF002A5C)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "📅 Ver Agenda General de Sede",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // ITEM 3: ALERTA DE BLOQUEO POR FALTA DE STOCK (SI APLICA)
            mensajeAlerta?.let { alerta ->
                item {
                    Text(
                        text = alerta,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ITEM 4: ENCABEZADO DE SECCIÓN
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔔 Solicitudes Recibidas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF002A5C)
                    )

                    Surface(
                        color = Color(0xFF002A5C),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${solicitudes.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // ITEM 5: TARJETAS DE SOLICITUDES
            if (solicitudes.isEmpty()) {
                item {
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
                                text = "No hay solicitudes pendientes de revisión.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(solicitudes, key = { it.id }) { solicitud ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSolicitudClick(solicitud) }
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
                                        "Aprobado" -> Color(0xFFE8F5E9)
                                        "Rechazado" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFFFE082)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = solicitud.estadoGeneral,
                                        color = when (solicitud.estadoGeneral) {
                                            "Aprobado" -> Color(0xFF2E7D32)
                                            "Rechazado" -> Color(0xFFC62828)
                                            else -> Color(0xFF5D4037)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "👤 Solicitante: ${solicitud.responsable} (${solicitud.escuelaArea})",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )

                            Text(
                                text = "📅 Fecha: ${solicitud.fechaTexto} | ⏰ Hora: ${solicitud.horaTexto}",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (solicitud.recursosSeleccionados.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Recursos solicitados:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
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

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val recursoSinStock = todosLosRecursos.find { recurso ->
                                            val stock = mapaStockGlobal[recurso.id] ?: 0
                                            val loPide = solicitud.recursosSeleccionados.any {
                                                it.contains(recurso.nombre, ignoreCase = true)
                                            }
                                            loPide && stock <= 0
                                        }

                                        if (recursoSinStock != null && solicitud.estadoGeneral != "Aprobado") {
                                            mensajeAlerta = "❌ No se puede aprobar: Sin stock de '${recursoSinStock.nombre}'"
                                        } else {
                                            mensajeAlerta = null
                                            onAprobarSolicitud(solicitud.id)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("✓ Aprobar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        mensajeAlerta = null
                                        onRechazarSolicitud(solicitud.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("✗ Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
                        mostrarDialogoCerrarSesion = false
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