package cl.duoc.aplicacionmoviltherionslabs.model

data class Recurso(
    val id: String,
    val nombre: String,
    val area: String,
    var seleccionado: Boolean = false
)

data class SolicitudActividad(
    val id: String,
    val nombreActividad: String,
    val responsable: String,
    val escuelaArea: String = "",
    val fechaTexto: String = "",
    val horaTexto: String = "",
    val recursosSeleccionados: List<String> = emptyList(),
    var estadoComunicaciones: String = "Pendiente",
    var estadoServiciosGenerales: String = "Pendiente",
    var estadoCTA: String = "Pendiente",
    var estadoPorteria: String = "Pendiente",
    var estadoExtension: String = "Pendiente"
) {
    val estadoGeneral: String
        get() = if (estadoComunicaciones == "Aprobado" &&
            estadoServiciosGenerales == "Aprobado" &&
            estadoCTA == "Aprobado" &&
            estadoPorteria == "Aprobado"
        ) "Aprobado" else "Pendiente"
}