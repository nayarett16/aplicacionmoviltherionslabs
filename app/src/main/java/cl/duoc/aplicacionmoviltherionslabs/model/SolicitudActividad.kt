package cl.duoc.aplicacionmoviltherionslabs.model

public data class SolicitudActividad(
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