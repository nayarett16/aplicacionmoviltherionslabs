package cl.duoc.aplicacionmoviltherionslabs.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudDto
import cl.duoc.aplicacionmoviltherionslabs.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SolicitudUiState(
    val listaSolicitudes: List<SolicitudDto> = emptyList(),
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class SolicitudViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun cargarSolicitudes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.solicitudesApi.obtenerTodas()
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            listaSolicitudes = response.body() ?: emptyList(),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Error del servidor: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Sin conexión al servidor (puerto 8083)")
                }
            }
        }
    }

    fun enviarSolicitud(correoUsuario: String, titulo: String, descripcion: String) {
        if (titulo.isBlank() || descripcion.isBlank()) {
            _uiState.update { it.copy(error = "Completa el título y la descripción") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, mensaje = null) }
            try {
                val nueva = SolicitudDto(
                    usuarioCorreo = correoUsuario,
                    titulo = titulo,
                    descripcion = descripcion
                )
                val response = RetrofitClient.solicitudesApi.crearSolicitud(nueva)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(isLoading = false, mensaje = "Solicitud guardada en Supabase")
                    }
                    cargarSolicitudes()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "No se pudo guardar (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error de conexión con 192.168.8.144:8083")
                }
            }
        }
    }
}