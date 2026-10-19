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
import cl.duoc.aplicacionmoviltherionslabs.data.RepositorioRecursos
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad

enum class TipoEntidad(val nombreArea: String, val correoMatch: String, val etiquetaRecurso: String) {
    CTA("CTA / Digitales", "cta", "CTA"),
    COMUNICACIONES("Comunicaciones", "comunicaciones", "COM"),
    SERVICIOS("Servicios Generales", "servicios", "SERVICIOS"),
    PORTERIA("Portería", "porteria", "PORTERIA");

    companion object {
        fun desdeCorreo(correo: String): TipoEntidad? {
            val c = correo.lowercase().trim()
            return values().find { c.contains(it.correoMatch) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntidadHomeScreen(
    entidad: TipoEntidad,
    solicitudes: List<SolicitudActividad>,
    onCambiarEstado: (idSolicitud: String, nuevoEstado: String) -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    var mensajeAlerta by remember { mutableStateOf<String?>(null) }

    // Cargar los recursos que administra la entidad
    val recursosEntidad = remember(entidad) {
        RepositorioRecursos.obtenerTodosLosRecursos().filter {
            it.area.lowercase().contains(entidad.correoMatch) ||
                    (entidad == TipoEntidad.CTA && it.area.contains("CTA", ignoreCase = true)) ||
                    (entidad == TipoEntidad.SERVICIOS && it.area.contains("Servicios", ignoreCase = true))
        }
    }

    // Mapa de stock restante ajustado por aprobaciones previa de esta entidad
    val mapaStockDisponible = remember(solicitudes, recursosEntidad) {
        recursosEntidad.associate { recurso ->
            val cantidadUsada = solicitudes.count { solicitud ->
                val estadoEntidad = when (entidad) {
                    TipoEntidad.CTA -> solicitud.estadoCTA
                    TipoEntidad.COMUNICACIONES -> solicitud.estadoComunicaciones
                    TipoEntidad.SERVICIOS -> solicitud.estadoServiciosGenerales
                    TipoEntidad.PORTERIA -> solicitud.estadoPorteria
                }
                estadoEntidad == "Aprobado" && solicitud.recursosSeleccionados.any { it.contains(recurso.nombre, ignoreCase = true) }
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
                        text = "Panel ${entidad.nombreArea}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onCerrarSesionClick) {
                        Text("Salir", color = Color.White, fontWeight = FontWeight.Bold)
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

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF002A5C)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "INVENTARIO Y STOCK DISPONIBLE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (recursosEntidad.isEmpty()) {
                        Text("No hay recursos registrados para esta área.", color = Color.White, fontSize = 12.sp)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recursosEntidad.forEach { recurso ->
                                val stock = mapaStockDisponible[recurso.id] ?: 0
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (stock > 0) Color(0xFF1E3A8A) else Color(0xFF7F1D1D)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = recurso.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Stock: $stock",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (stock > 0) Color(0xFF4ADE80) else Color(0xFFFCA5A5)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            mensajeAlerta?.let { alerta ->
                Text(
                    text = alerta,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Solicitudes Recibidas",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF002A5C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (solicitudes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay solicitudes registradas.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(solicitudes) { solicitud ->
                        // FILTRAR UNICAMENTE LOS RECURSOS QUE PERTENECEN A ESTA ENTIDAD
                        val recursosDelArea = solicitud.recursosSeleccionados.filter { textoRecurso ->
                            textoRecurso.contains("(${entidad.etiquetaRecurso})", ignoreCase = true) ||
                                    recursosEntidad.any { r -> textoRecurso.contains(r.nombre, ignoreCase = true) }
                        }

                        // Solo renderizar si la solicitud efectivamente pide elementos de esta área
                        if (recursosDelArea.isNotEmpty()) {
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
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = solicitud.nombreActividad,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF002A5C)
                                    )
                                    Text(
                                        text = " ${solicitud.fechaTexto} |  ${solicitud.horaTexto}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // MUESTRA EXCLUSIVAMENTE LO QUE CORRESPONDE A ESTA ÁREA
                                    Text(
                                        text = "Recursos Solicitados (${entidad.nombreArea}):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF002A5C)
                                    )
                                    recursosDelArea.forEach { rec ->
                                        Text(
                                            text = "• $rec",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Estado: $estadoActual",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (estadoActual == "Aprobado") Color(0xFF2E7D32) else Color(0xFFC62828)
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = {
                                                    val sinStock = recursosEntidad.filter { recurso ->
                                                        val stock = mapaStockDisponible[recurso.id] ?: 0
                                                        val loPide = recursosDelArea.any { it.contains(recurso.nombre, ignoreCase = true) }
                                                        loPide && stock <= 0
                                                    }

                                                    if (sinStock.isNotEmpty() && estadoActual != "Aprobado") {
                                                        mensajeAlerta = " Sin stock suficiente de '${sinStock.first().nombre}'"
                                                    } else {
                                                        mensajeAlerta = null
                                                        onCambiarEstado(solicitud.id, "Aprobado")
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                            ) {
                                                Text("Aprobar", fontSize = 11.sp, color = Color.White)
                                            }

                                            Button(
                                                onClick = {
                                                    mensajeAlerta = null
                                                    onCambiarEstado(solicitud.id, "Rechazado")
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                                            ) {
                                                Text("Rechazar", fontSize = 11.sp, color = Color.White)
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
}