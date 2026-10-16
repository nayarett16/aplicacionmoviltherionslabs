package cl.duoc.aplicacionmoviltherionslabs.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmacionDialog(
    titulo: String,
    mensaje: String,
    textoConfirmar: String = "Confirmar",
    textoCancelar: String = "Cancelar",
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titulo) },
        text = { Text(text = mensaje) },
        confirmButton = {
            Button(onClick = onConfirmar) {
                Text(text = textoConfirmar)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = textoCancelar)
            }
        }
    )
}
