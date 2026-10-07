package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DisponibleVerde
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocErrorRed

data class SolicitudItem(
    val id: Int,
    val profesor: String,
    val sala: String,
    val fechaHora: String,
    val motivo: String,
    var estado: String // "Pendiente", "Aprobada", "Rechazada"
)

data class SalaItem(
    val codigo: String,
    val tipo: String,
    var equipamiento: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinadorHomeScreen(
    onCerrarSesionClick: () -> Unit = {}
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val titulosTabs = listOf("Solicitudes", "Reserva Directa", "Gestión Salas")

    // Estado simulado de solicitudes recibidas
    val listaSolicitudes = remember {
        mutableStateListOf(
            SolicitudItem(1, "Prof. Carlos Mendoza", "LC25 - Laboratorio Computación", "15/10/2026 - 08:30 hrs", "Examen práctico PGY3221", "Pendiente"),
            SolicitudItem(2, "Prof. Andrea Silva", "AUD01 - Auditorio Central", "16/10/2026 - 11:00 hrs", "Charla IOT & Redes", "Pendiente"),
            SolicitudItem(3, "Prof. Roberto Gómez", "LAB03 - Taller Prototipado", "18/10/2026 - 14:00 hrs", "Taller TherionLabs VR", "Aprobada")
        )
    }

    // Estado simulado de salas y equipamiento
    val listaSalas = remember {
        mutableStateListOf(
            SalaItem("LC25", "Laboratorio PC", mutableStateListOf("Proyector 4K", "30 PCs Core i7", "Aire Acondicionado")),
            SalaItem("AUD01", "Auditorio", mutableStateListOf("Micrófono Inalámbrico", "Amplificación 5.1", "Escenario")),
            SalaItem("LAB03", "Taller Mac", mutableStateListOf("15 iMac M2", "Impresora 3D", "Smart TV 75\""))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Coordinador", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text("Gestión de Infraestructura & Reservas", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesionClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = tabSeleccionada) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }
            }

            when (tabSeleccionada) {
                0 -> TabSolicitudes(listaSolicitudes)
                1 -> TabReservaDirecta(
                    onReservaCreada = { nuevaSolicitud ->
                        listaSolicitudes.add(0, nuevaSolicitud)
                        tabSeleccionada = 0
                    }
                )
                2 -> TabGestionSalas(listaSalas)
            }
        }
    }
}

// --- TAB 1: APROBAR / RECHAZAR SOLICITUDES DE RELATORES ---
@Composable
fun TabSolicitudes(solicitudes: List<SolicitudItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(solicitudes) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.profesor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        BadgeEstado(item.estado)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sala: ${item.sala}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Horario: ${item.fechaHora}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Motivo: ${item.motivo}", style = MaterialTheme.typography.bodySmall)

                    if (item.estado == "Pendiente") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { item.estado = "Aprobada" },
                                colors = ButtonDefaults.buttonColors(containerColor = DisponibleVerde),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aprobar")
                            }

                            Button(
                                onClick = { item.estado = "Rechazada" },
                                colors = ButtonDefaults.buttonColors(containerColor = DuocErrorRed),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 2: CREAR RESERVA DIRECTA (BLOQUEO INMEDIATO PARA RELATOR) ---
@Composable
fun TabReservaDirecta(onReservaCreada: (SolicitudItem) -> Unit) {
    var sala by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Bloquear / Reservar Sala Inmediata", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Esta reserva quedará en estado APROBADA de inmediato y bloqueará la sala para los profesores.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        OutlinedTextField(
            value = sala,
            onValueChange = { sala = it },
            label = { Text("Código de Sala (ej: LC25, AUD01)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha y Bloque Horario") },
            placeholder = { Text("Ej: 20/10/2026 - 10:00 a 12:00") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            label = { Text("Motivo / Evento Académico Institucional") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Button(
            onClick = {
                if (sala.isNotBlank() && fecha.isNotBlank()) {
                    onReservaCreada(
                        SolicitudItem(
                            id = (100..999).random(),
                            profesor = "Coordinación Académica (Bloqueo)",
                            sala = sala,
                            fechaHora = fecha,
                            motivo = motivo.ifBlank { "Mantenimiento / Evento Oficial" },
                            estado = "Aprobada"
                        )
                    )
                }
            },
            enabled = sala.isNotBlank() && fecha.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bloquear y Aprobar Inmediatamente")
        }
    }
}

// --- TAB 3: AGREGAR SALAS Y EQUIPAMIENTO NUEVO ---
@Composable
fun TabGestionSalas(salas: MutableList<SalaItem>) {
    var nuevaSalaCodigo by remember { mutableStateOf("") }
    var nuevaSalaTipo by remember { mutableStateOf("") }
    var salaSeleccionadaEquip by remember { mutableStateOf("") }
    var nuevoEquipamiento by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formulario Agregar Nueva Sala
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("➕ Agregar Nueva Sala", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevaSalaCodigo,
                        onValueChange = { nuevaSalaCodigo = it },
                        label = { Text("Código Sala") },
                        placeholder = { Text("Ej: LAB05") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = nuevaSalaTipo,
                        onValueChange = { nuevaSalaTipo = it },
                        label = { Text("Tipo") },
                        placeholder = { Text("Ej: Taller") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        if (nuevaSalaCodigo.isNotBlank()) {
                            salas.add(SalaItem(nuevaSalaCodigo, nuevaSalaTipo.ifBlank { "Multipropósito" }, mutableStateListOf()))
                            nuevaSalaCodigo = ""
                            nuevaSalaTipo = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Crear Sala")
                }
            }
        }

        // Formulario Agregar Equipamiento
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🔌 Añadir Equipamiento a Sala Existente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = salaSeleccionadaEquip,
                    onValueChange = { salaSeleccionadaEquip = it },
                    label = { Text("Código de Sala (ej: LC25)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nuevoEquipamiento,
                    onValueChange = { nuevoEquipamiento = it },
                    label = { Text("Nuevo Equipamiento / Recurso") },
                    placeholder = { Text("Ej: Tableta Digitalizadora") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val salaEncontrada = salas.find { it.codigo.equals(salaSeleccionadaEquip.trim(), ignoreCase = true) }
                        if (salaEncontrada != null && nuevoEquipamiento.isNotBlank()) {
                            (salaEncontrada.equipamiento as MutableList).add(nuevoEquipamiento)
                            nuevoEquipamiento = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Asignar Equipamiento")
                }
            }
        }

        Text("Salas Registradas y Equipamiento Actual", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        salas.forEach { sala ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${sala.codigo} (${sala.tipo})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Equipamiento: " + if (sala.equipamiento.isEmpty()) "Sin equipamiento registrado" else sala.equipamiento.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun BadgeEstado(estado: String) {
    val colorFondo = when (estado) {
        "Aprobada" -> DisponibleVerde
        "Rechazada" -> DuocErrorRed
        else -> Color(0xFFFF9800)
    }

    Box(
        modifier = Modifier
            .background(colorFondo, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(estado, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}