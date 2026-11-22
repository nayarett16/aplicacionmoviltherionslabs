package cl.duoc.aplicacionmoviltherionslabs.model

data class SolicitudDto(
    val id: Long? = null,
    val usuarioCorreo: String,
    val titulo: String,
    val descripcion: String,
    val estado: String = "PENDIENTE",
    val fechaCreacion: String? = null
)