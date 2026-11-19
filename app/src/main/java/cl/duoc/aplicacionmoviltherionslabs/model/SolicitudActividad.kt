package cl.duoc.aplicacionmoviltherionslabs.model

import com.google.gson.annotations.SerializedName

// Modelo interno de tu UI (no se toca para no romper tus pantallas)
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

// Modelo limpio para enviar por red hacia Supabase (sin lugar/descripcion y con texto plano)
data class SolicitudRequestDto(
    val id: String,
    @SerializedName("nombre_actividad") val nombreActividad: String,
    val responsable: String,
    @SerializedName("escuela_area") val escuelaArea: String,
    @SerializedName("fecha_texto") val fechaTexto: String,
    @SerializedName("hora_texto") val horaTexto: String,
    @SerializedName("estado_general") val estadoGeneral: String,
    @SerializedName("estado_cta") val estadoCTA: String,
    @SerializedName("estado_comunicaciones") val estadoComunicaciones: String,
    @SerializedName("estado_servicios_generales") val estadoServiciosGenerales: String,
    @SerializedName("estado_porteria") val estadoPorteria: String,
    @SerializedName("recursos_seleccionados") val recursosSeleccionados: String
)

// Convierte automáticamente tu modelo de UI al formato de Supabase
fun SolicitudActividad.toRequestDto(): SolicitudRequestDto {
    return SolicitudRequestDto(
        id = this.id,
        nombreActividad = this.nombreActividad,
        responsable = this.responsable,
        escuelaArea = this.escuelaArea,
        fechaTexto = this.fechaTexto,
        horaTexto = this.horaTexto,
        estadoGeneral = this.estadoGeneral,
        estadoCTA = this.estadoCTA,
        estadoComunicaciones = this.estadoComunicaciones,
        estadoServiciosGenerales = this.estadoServiciosGenerales,
        estadoPorteria = this.estadoPorteria,
        recursosSeleccionados = this.recursosSeleccionados.joinToString(", ")
    )
}