package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aplicacionmoviltherionslabs.data.NotificationHelper
import cl.duoc.aplicacionmoviltherionslabs.model.RecursoModel
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import cl.duoc.aplicacionmoviltherionslabs.ui.components.ConfirmacionDialog
import cl.duoc.aplicacionmoviltherionslabs.ui.components.SeleccionadorFechaDialog
import cl.duoc.aplicacionmoviltherionslabs.ui.components.SeleccionadorHoraDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    onVolverClick: () -> Unit = {},
    onVerCalendarioClick: () -> Unit = {},
    onSolicitudCreada: (SolicitudActividad) -> Unit = {}
) {
    // RECURSOS NATIVOS
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var nombreActividad by remember { mutableStateOf("") }
    var responsable by remember { mutableStateOf("") }
    var escuelaArea by remember { mutableStateOf("") }

    var fechaTexto by remember { mutableStateOf("Seleccionar Fecha") }
    var horaTexto by remember { mutableStateOf("Seleccionar Hora") }
    var fechaMillis by remember { mutableStateOf<Long?>(null) }
    var horaSeleccionada by remember { mutableStateOf(false) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    // Feriados nacionales oficiales de Chile (formato dd/MM)
    val feriadosChile = remember {
        setOf(
            "01/01", "03/04", "04/04", "01/05", "21/05", "21/06",
            "29/06", "16/07", "15/08", "18/09", "19/09", "12/10",
            "31/10", "01/11", "08/12", "25/12"
        )
    }

    // Lista de recursos
    val recursosCatalogo = remember {
        listOf(
            RecursoModel("1", "Micrófono Inalámbrico", "Área: CTA / Digitales", 5, 5),
            RecursoModel("2", "Proyector Portátil", "Área: CTA / Digitales", 3, 3),
            RecursoModel("3", "Cobertura Fotográfica", "Área: Comunicaciones", 2, 2),
            RecursoModel("4", "Apoyo Aseo / Camarines", "Área: Servicios Generales", 10, 10),
            RecursoModel("5", "Acceso / Control Público", "Área: Portería", 4, 4)
        )
    }

    // Control de selección local mediante IDs
    var idsSeleccionados by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solicitud de Evento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    TextButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onVolverClick()
                    }) {
                        Text("← Volver", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onVerCalendarioClick()
                    }) {
                        Text("📅 Agenda", fontWeight = FontWeight.Bold, color = Color.White)
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
            OutlinedTextField(
                value = nombreActividad,
                onValueChange = { nombreActividad = it },
                label = { Text("Nombre de la Actividad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = responsable,
                onValueChange = { responsable = it },
                label = { Text("Docente / Responsable Cargo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = escuelaArea,
                onValueChange = { escuelaArea = it },
                label = { Text("Escuela o Área Solicitante") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "⚠️ Mínimo 2 semanas de anticipación. Sin reservas domingos ni feriados.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { mostrarDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = fechaTexto, fontSize = 12.sp, color = Color(0xFF002A5C))
                }

                OutlinedButton(
                    onClick = { mostrarTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = horaTexto, fontSize = 12.sp, color = Color(0xFF002A5C))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recursos Requeridos por Área:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF002A5C)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recursosCatalogo) { recurso ->
                    val estaSeleccionado = idsSeleccionados.contains(recurso.id)
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recurso.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF002A5C)
                                )
                                Text(
                                    text = recurso.area,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            Checkbox(
                                checked = estaSeleccionado,
                                onCheckedChange = { checked ->
                                    idsSeleccionados = if (checked) {
                                        idsSeleccionados + recurso.id
                                    } else {
                                        idsSeleccionados - recurso.id
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF002A5C))
                            )
                        }
                    }
                }
            }

            mensajeError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (nombreActividad.isBlank() || responsable.isBlank() || escuelaArea.isBlank()) {
                        mensajeError = "Por favor completa todos los campos de texto."
                    } else if (fechaMillis == null || !horaSeleccionada) {
                        mensajeError = "Debes seleccionar fecha y hora para la actividad."
                    } else if (idsSeleccionados.isEmpty()) {
                        mensajeError = "Debes seleccionar al menos un recurso."
                    } else {
                        mensajeError = null
                        mostrarConfirmacion = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107),
                    contentColor = Color(0xFF002A5C)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Enviar Solicitud a Jefaturas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    if (mostrarDatePicker) {
        SeleccionadorFechaDialog(
            onFechaSeleccionada = { millis ->
                millis?.let {
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = it
                    }
                    val diaSemana = cal.get(Calendar.DAY_OF_WEEK)

                    val formatoDiaMes = SimpleDateFormat("dd/MM", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.format(Date(it))

                    val formatoCompleto = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.format(Date(it))

                    if (diaSemana == Calendar.SUNDAY) {
                        mensajeError = "❌ No se permiten reservas los días domingo."
                        fechaMillis = null
                        fechaTexto = "Seleccionar Fecha"
                    } else if (feriadosChile.contains(formatoDiaMes)) {
                        mensajeError = "❌ La fecha elegida corresponde a un feriado institucional."
                        fechaMillis = null
                        fechaTexto = "Seleccionar Fecha"
                    } else {
                        mensajeError = null
                        fechaMillis = it
                        fechaTexto = formatoCompleto
                    }
                }
            },
            onDismiss = { mostrarDatePicker = false }
        )
    }

    if (mostrarTimePicker) {
        SeleccionadorHoraDialog(
            onHoraSeleccionada = { hora, minuto ->
                horaTexto = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                horaSeleccionada = true
            },
            onDismiss = { mostrarTimePicker = false }
        )
    }

    if (mostrarConfirmacion) {
        ConfirmacionDialog(
            titulo = "Confirmar Envío",
            mensaje = "¿Confirmas el envío de '$nombreActividad' para la fecha $fechaTexto a las $horaTexto?",
            textoConfirmar = "Enviar",
            onConfirmar = {
                mostrarConfirmacion = false

                // 1. RECURSO NATIVO: Vibración física del hardware
                val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        android.os.VibrationEffect.createOneShot(
                            500,
                            android.os.VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }

                // 2. RECURSO NATIVO: Notificación Local del Sistema
                NotificationHelper.mostrarNotificacionSolicitud(
                    context = context,
                    titulo = "¡Solicitud Enviada con Éxito! 🚀",
                    mensaje = "Tu solicitud '$nombreActividad' ha sido registrada correctamente."
                )

                val nombresRecursosSeleccionados = recursosCatalogo
                    .filter { idsSeleccionados.contains(it.id) }
                    .map { it.nombre }

                val nuevaSolicitud = SolicitudActividad(
                    id = System.currentTimeMillis().toString(),
                    nombreActividad = nombreActividad,
                    responsable = responsable,
                    escuelaArea = escuelaArea,
                    fechaTexto = fechaTexto,
                    horaTexto = horaTexto,
                    recursosSeleccionados = nombresRecursosSeleccionados,
                    estadoComunicaciones = "Pendiente",
                    estadoServiciosGenerales = "Pendiente",
                    estadoCTA = "Pendiente",
                    estadoPorteria = "Pendiente"
                )
                onSolicitudCreada(nuevaSolicitud)
            },
            onDismiss = { mostrarConfirmacion = false }
        )
    }
}