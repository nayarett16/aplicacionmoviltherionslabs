package cl.duoc.aplicacionmoviltherionslabs.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionadorFechaDialog(
    onFechaSeleccionada: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // Calculamos la fecha mínima permitida (Hoy + 14 días)
    val fechaMinimaMillis = remember {
        val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendario.add(Calendar.DAY_OF_YEAR, 14) // Suma 2 semanas exactas
        // Ajustamos la hora al inicio del día UTC para evitar desfasajes
        calendario.set(Calendar.HOUR_OF_DAY, 0)
        calendario.set(Calendar.MINUTE, 0)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        calendario.timeInMillis
    }

    // Estado del DatePicker restringiendo fechas anteriores al límite de 2 semanas
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= fechaMinimaMillis
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onFechaSeleccionada(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}