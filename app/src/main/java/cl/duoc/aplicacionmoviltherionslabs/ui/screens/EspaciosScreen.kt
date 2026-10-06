package cl.duoc.aplicacionmoviltherionslabs.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.data.RoomRepository
import cl.duoc.aplicacionmoviltherionslabs.model.Sala

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspaciosScreen(
    onVolverClick: () -> Unit = {}
) {
    // Estados de búsqueda y filtros
    var textoBusqueda by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    var soloDisponibles by remember { mutableStateOf(false) }
    var caracteristicaSeleccionada by remember { mutableStateOf<String?>(null) }
    var capacidadMinima by remember { mutableFloatStateOf(0f) }

    val todasLasSalas = remember { RoomRepository.obtenerSalas() }

    // Listas fijas de opciones para los chips
    val tipos = listOf("Sala de clases", "Laboratorio", "Auditorio", "Multipropósito", "Taller")
    val caracteristicas = listOf("Proyector", "Computadores", "Climatización", "Videoconferencia")

    // Lógica de filtrado dinámico
    val salasFiltradas = todasLasSalas.filter { sala ->
        val cumpleTexto = textoBusqueda.isEmpty() ||
                sala.codigo.contains(textoBusqueda, ignoreCase = true) ||
                sala.nombre.contains(textoBusqueda, ignoreCase = true)

        val cumpleTipo = tipoSeleccionado == null || sala.tipo.equals(tipoSeleccionado, ignoreCase = true)
        val cumpleDisponible = !soloDisponibles || sala.disponibleAhora
        val cumpleCaracteristica = caracteristicaSeleccionada == null || sala.caracteristicas.contains(caracteristicaSeleccionada)
        val cumpleCapacidad = sala.capacidad >= capacidadMinima.toInt()

        cumpleTexto && cumpleTipo && cumpleDisponible && cumpleCaracteristica && cumpleCapacidad
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Espacios", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Relator Demo A • Relator", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // --- BUSCADOR POR TEXTO O CÓDIGO ---
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por código (ej: AUD-01, LC25) o nombre...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { textoBusqueda = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // --- FILTROS RÁPIDOS (FILA 1: ESTADO Y TIPOS) ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = soloDisponibles,
                        onClick = { soloDisponibles = !soloDisponibles },
                        label = { Text("Libres ahora") }
                    )
                }
                items(tipos) { tipo ->
                    FilterChip(
                        selected = tipoSeleccionado == tipo,
                        onClick = {
                            tipoSeleccionado = if (tipoSeleccionado == tipo) null else tipo
                        },
                        label = { Text(tipo) }
                    )
                }
            }

            // --- FILTROS RÁPIDOS (FILA 2: EQUIPAMIENTO) ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(caracteristicas) { equipamiento ->
                    FilterChip(
                        selected = caracteristicaSeleccionada == equipamiento,
                        onClick = {
                            caracteristicaSeleccionada = if (caracteristicaSeleccionada == equipamiento) null else equipamiento
                        },
                        label = { Text(equipamiento) }
                    )
                }
            }

            // --- CONTROLES DE CAPACIDAD Y LIMPIAR ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (capacidadMinima == 0f) "Capacidad: cualquiera" else "Capacidad mínima: ${capacidadMinima.toInt()} personas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (tipoSeleccionado != null || soloDisponibles || caracteristicaSeleccionada != null || capacidadMinima > 0f || textoBusqueda.isNotEmpty()) {
                    TextButton(onClick = {
                        textoBusqueda = ""
                        tipoSeleccionado = null
                        soloDisponibles = false
                        caracteristicaSeleccionada = null
                        capacidadMinima = 0f
                    }) {
                        Text("Limpiar")
                    }
                }
            }

            Slider(
                value = capacidadMinima,
                onValueChange = { capacidadMinima = it },
                valueRange = 0f..150f,
                steps = 14
            )

            // --- CONTADOR DE RESULTADOS ---
            Text(
                text = "${salasFiltradas.size} de ${todasLasSalas.size} espacios",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // --- LISTADO DE TARJETAS DE SALAS ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(salasFiltradas) { sala ->
                    TarjetaSalaItem(sala = sala)
                }
            }
        }
    }
}

@Composable
fun TarjetaSalaItem(sala: Sala) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sala.codigo,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (sala.disponibleAhora) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Libre ahora", fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                } else {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Ocupada", fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }

            Text(
                text = sala.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${sala.tipo} • Piso ${sala.piso} • ${sala.capacidad} personas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = sala.caracteristicas.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}