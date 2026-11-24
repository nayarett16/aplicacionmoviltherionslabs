package cl.duoc.aplicacionmoviltherionslabs.model

// Modelo interno de tu UI (se mantiene intacto para no romper tus pantallas)
data class SolicitudActividad(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombreActividad: String = "",
    val responsable: String = "",
    val escuelaArea: String = "",
    val fechaTexto: String = "",
    val horaTexto: String = "",
    val lugar: String = "",
    val descripcion: String = "",
    val estadoGeneral: String = "Pendiente",
    val estadoCTA: String = "Pendiente",
    val estadoComunicaciones: String = "Pendiente",
    val estadoServiciosGenerales: String = "Pendiente",
    val estadoPorteria: String = "Pendiente",
    val recursosSeleccionados: List<String> = emptyList()
)

// DTO exacto en camelCase que coincide con Solicitud.kt de Spring Boot
data class SolicitudRequestDto(
    val id: String = "",
    val nombreActividad: String = "",
    val responsable: String = "",
    val escuelaArea: String = "",
    val fechaTexto: String = "",
    val horaTexto: String = "",
    val recursosSeleccionados: String = "",
    val estadoGeneral: String = "PENDIENTE",
    val estadoCta: String = "PENDIENTE",
    val estadoComunicaciones: String = "PENDIENTE",
    val estadoServiciosGenerales: String = "PENDIENTE",
    val estadoPorteria: String = "PENDIENTE"
)

// Convierte de UI -> Backend (para POST)
fun SolicitudActividad.toRequestDto(): SolicitudRequestDto {
    return SolicitudRequestDto(
        id = this.id,
        nombreActividad = this.nombreActividad,
        responsable = this.responsable,
        escuelaArea = this.escuelaArea,
        fechaTexto = this.fechaTexto,
        horaTexto = this.horaTexto,
        recursosSeleccionados = this.recursosSeleccionados.joinToString(", "),
        estadoGeneral = this.estadoGeneral,
        estadoCta = this.estadoCTA,
        estadoComunicaciones = this.estadoComunicaciones,
        estadoServiciosGenerales = this.estadoServiciosGenerales,
        estadoPorteria = this.estadoPorteria
    )
}

// Convierte de Backend -> UI (para GET)
fun SolicitudRequestDto.toUiModel(): SolicitudActividad {
    return SolicitudActividad(
        id = this.id,
        nombreActividad = this.nombreActividad,
        responsable = this.responsable,
        escuelaArea = this.escuelaArea,
        fechaTexto = this.fechaTexto,
        horaTexto = this.horaTexto,
        estadoGeneral = this.estadoGeneral,
        estadoCTA = this.estadoCta,
        estadoComunicaciones = this.estadoComunicaciones,
        estadoServiciosGenerales = this.estadoServiciosGenerales,
        estadoPorteria = this.estadoPorteria,
        recursosSeleccionados = if (this.recursosSeleccionados.isBlank()) {
            emptyList()
        } else {
            this.recursosSeleccionados.split(",").map { it.trim() }
        }
    )
}