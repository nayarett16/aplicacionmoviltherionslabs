package cl.duoc.aplicacionmoviltherionslabs.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocBlueAccent
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocErrorRed
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocNavy
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocSurfaceDark
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocTextSecondary
import cl.duoc.aplicacionmoviltherionslabs.ui.theme.DuocTextWhite
import cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel.LoginViewModel

// Funciones auxiliares para guardar y leer correos guardados localmente
private fun obtenerCorreosGuardados(context: Context): List<String> {
    val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val setCorreos = prefs.getStringSet("correos_frecuentes", emptySet()) ?: emptySet()
    return setCorreos.toList()
}

private fun guardarCorreo(context: Context, correo: String) {
    if (correo.isBlank()) return
    val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val actual = prefs.getStringSet("correos_frecuentes", emptySet())?.toMutableSet() ?: mutableSetOf()
    actual.add(correo.trim())
    prefs.edit().putStringSet("correos_frecuentes", actual).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit = {}
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estado local para los correos recordados y el menú desplegable
    var isExpanded by remember { mutableStateOf(false) }
    val correosGuardados = remember { mutableStateListOf<String>() }

    // Cargar la lista al iniciar la pantalla
    LaunchedEffect(Unit) {
        correosGuardados.clear()
        correosGuardados.addAll(obtenerCorreosGuardados(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuocNavy)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Logo Reserva",
            tint = DuocBlueAccent,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reserva de espacios",
            color = DuocTextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "THERIONLABS",
            color = DuocTextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo con desplegable para recordar/sugerir correos
        ExposedDropdownMenuBox(
            expanded = isExpanded && correosGuardados.isNotEmpty(),
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = {
                    loginViewModel.onEmailChanged(it)
                    isExpanded = true
                },
                label = { Text("Correo corporativo", color = DuocTextSecondary) },
                singleLine = true,
                isError = uiState.emailError != null,
                trailingIcon = {
                    if (uiState.emailError != null) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = DuocErrorRed)
                    } else if (correosGuardados.isNotEmpty()) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DuocBlueAccent,
                    unfocusedBorderColor = DuocTextSecondary,
                    focusedTextColor = DuocTextWhite,
                    unfocusedTextColor = DuocTextWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            // Menú con la lista de sugerencias
            val filtrados = correosGuardados.filter { it.contains(uiState.email, ignoreCase = true) }
            if (filtrados.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.background(DuocSurfaceDark)
                ) {
                    filtrados.forEach { sugerencia ->
                        DropdownMenuItem(
                            text = { Text(sugerencia, color = DuocTextWhite) },
                            onClick = {
                                loginViewModel.onEmailChanged(sugerencia)
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (uiState.emailError != null) {
            Text(
                text = uiState.emailError!!,
                color = DuocErrorRed,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { loginViewModel.onPasswordChanged(it) },
            label = { Text("Contraseña", color = DuocTextSecondary) },
            singleLine = true,
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = uiState.passwordError != null,
            trailingIcon = {
                TextButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                    Text(if (uiState.isPasswordVisible) "Ocultar" else "Ver", color = DuocBlueAccent)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuocBlueAccent,
                unfocusedBorderColor = DuocTextSecondary,
                focusedTextColor = DuocTextWhite,
                unfocusedTextColor = DuocTextWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.passwordError != null) {
            Text(
                text = uiState.passwordError!!,
                color = DuocErrorRed,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.onLoginClicked()
                if (uiState.isSuccess) {
                    // Guarda el correo en SharedPreferences al iniciar sesión correctamente
                    guardarCorreo(context, uiState.email)
                    onLoginSuccess(uiState.email)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DuocBlueAccent),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Ingresar", color = DuocNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DuocSurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cuentas ficticias de prueba (clave demo1234)",
                    color = DuocTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Consulta: consulta@therionlabs.test", color = DuocBlueAccent, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Relator: relator@therionlabs.test", color = DuocBlueAccent, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Coordinador: coordinacion@therionlabs.test", color = DuocBlueAccent, fontSize = 12.sp)
            }
        }
    }
}