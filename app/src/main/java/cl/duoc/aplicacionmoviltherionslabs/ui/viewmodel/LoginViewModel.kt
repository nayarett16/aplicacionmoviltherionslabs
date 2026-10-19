package cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Cuentas de prueba oficiales
    private val cuentasPrueba = setOf(
        "comunidad@duocuc.cl",
        "cta@duocuc.cl",
        "comunicaciones@duocuc.cl",
        "servicios@duocuc.cl",
        "porteria@duocuc.cl",
        "extension@duocuc.cl"
    )

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked() {
        val currentEmail = _uiState.value.email.trim().lowercase()
        val currentPassword = _uiState.value.password

        var hasError = false
        var emailErr: String? = null
        var passErr: String? = null

        if (currentEmail.isBlank()) {
            emailErr = "El correo es obligatorio"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            emailErr = "Ingrese un correo válido"
            hasError = true
        } else if (!currentEmail.endsWith("@duocuc.cl") && !currentEmail.endsWith("@duoc.cl")) {
            emailErr = "Debe usar un correo institucional @duocuc.cl"
            hasError = true
        } else if (!cuentasPrueba.contains(currentEmail) && !currentEmail.contains("profesor")) {
            emailErr = "Cuenta no registrada en el sistema de reservas"
            hasError = true
        }

        if (currentPassword.isBlank()) {
            passErr = "La contraseña es obligatoria"
            hasError = true
        } else if (currentPassword.length < 6) {
            passErr = "Debe tener al menos 6 caracteres"
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passErr, isSuccess = false) }
        } else {
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }

    // 🚀 FUNCIÓN CLAVE PARA CERRAR SESIÓN DE FORMA LIMPIA
    fun cerrarSesion() {
        _uiState.update {
            LoginUiState(
                email = "",
                password = "",
                isPasswordVisible = false,
                emailError = null,
                passwordError = null,
                isLoading = false,
                isSuccess = false
            )
        }
    }
}