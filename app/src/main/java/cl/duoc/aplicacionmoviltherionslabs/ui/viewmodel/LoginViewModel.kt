package cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aplicacionmoviltherionslabs.network.LoginRequest
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

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
        } else if (!emailRegex.matches(currentEmail)) {
            emailErr = "Ingrese un correo válido"
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
            return
        }

        // Llamada real al microservicio de Usuarios (puerto 8081)
        _uiState.update { it.copy(isLoading = true, emailError = null, passwordError = null) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.usuariosApi.login(
                    LoginRequest(correo = currentEmail, password = currentPassword)
                )
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            passwordError = "Correo o contraseña incorrectos en el servidor",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Respaldo local si el microservicio no está encendido
                if (cuentasPrueba.contains(currentEmail) && currentPassword == "demo1234") {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            emailError = "No se pudo conectar al servidor (8081)",
                            isSuccess = false
                        )
                    }
                }
            }
        }
    }

    fun cerrarSesion() {
        _uiState.update { LoginUiState() }
    }
}