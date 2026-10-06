package cl.duoc.aplicacionmoviltherionslabs.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    onVolverClick: () -> Unit = {},
    onSolicitudEnviada: () -> Unit = {}
) {
    var docenteNombre by remember { mutableStateOf("Relator Demo A") }
    var asignatura by remember { mutableStateOf("") }
    var salaCodigo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    var mostrarExito by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nueva Solicitud", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                        Text("Reserva de Espacio Académico", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        if (mostrarExito) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Solicitud Registrada!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La reserva para la sala $salaCodigo ha sido enviada a revisión académica.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onSolicitudEnviada,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Inicio")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = docenteNombre,
                    onValueChange = { docenteNombre = it },
                    label = { Text("Nombre del Docente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = asignatura,
                    onValueChange = { asignatura = it },
                    label = { Text("Asignatura / Sigla") },
                    placeholder = { Text("Ej: PGY3221 - DSY2201") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = salaCodigo,
                    onValueChange = { salaCodigo = it },
                    label = { Text("Código de Sala requerida") },
                    placeholder = { Text("Ej: LC25, AUD01") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (DD/MM/AAAA)") },
                    placeholder = { Text("Ej: 15/10/2026") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = { horaInicio = it },
                        label = { Text("Hora Inicio") },
                        placeholder = { Text("08:30") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = horaFin,
                        onValueChange = { horaFin = it },
                        label = { Text("Hora Fin") },
                        placeholder = { Text("10:00") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo / Observaciones") },
                    placeholder = { Text("Ej: Evaluación práctica de desarrollo móvil") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (asignatura.isNotBlank() && salaCodigo.isNotBlank()) {
                            mostrarExito = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = asignatura.isNotBlank() && salaCodigo.isNotBlank()
                ) {
                    Text("Enviar Solicitud", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}