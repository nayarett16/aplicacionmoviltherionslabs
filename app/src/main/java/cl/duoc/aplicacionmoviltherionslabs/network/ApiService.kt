package cl.duoc.aplicacionmoviltherionslabs.network

import cl.duoc.aplicacionmoviltherionslabs.model.RecursoModel
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val correo: String,
    val password: String
)

data class UsuarioResponse(
    val id: Int,
    val correo: String,
    val password: String,
    val nombre: String,
    val rol: String,
    val areaEntidad: String?
)

// DTO que coincide 1:1 con el JSON de catalogo-service (Spring Boot)
data class SolicitudDto(
    val id: String,
    val nombreActividad: String,
    val responsable: String,
    val escuelaArea: String,
    val fechaTexto: String,
    val horaTexto: String,
    val recursosSeleccionados: String,
    val estadoGeneral: String,
    val estadoCta: String,
    val estadoComunicaciones: String,
    val estadoServiciosGenerales: String,
    val estadoPorteria: String
)

// Extensiones para convertir sin romper tus pantallas de Android
fun SolicitudActividad.toDto(): SolicitudDto = SolicitudDto(
    id = id,
    nombreActividad = nombreActividad,
    responsable = responsable,
    escuelaArea = escuelaArea,
    fechaTexto = fechaTexto,
    horaTexto = horaTexto,
    recursosSeleccionados = recursosSeleccionados.joinToString(", "),
    estadoGeneral = estadoGeneral,
    estadoCta = estadoCTA,
    estadoComunicaciones = estadoComunicaciones,
    estadoServiciosGenerales = estadoServiciosGenerales,
    estadoPorteria = estadoPorteria
)

fun SolicitudDto.toModel(): SolicitudActividad = SolicitudActividad(
    id = id,
    nombreActividad = nombreActividad,
    responsable = responsable,
    escuelaArea = escuelaArea,
    fechaTexto = fechaTexto,
    horaTexto = horaTexto,
    estadoGeneral = estadoGeneral,
    estadoCTA = estadoCta,
    estadoComunicaciones = estadoComunicaciones,
    estadoServiciosGenerales = estadoServiciosGenerales,
    estadoPorteria = estadoPorteria,
    recursosSeleccionados = if (recursosSeleccionados.isBlank()) emptyList()
    else recursosSeleccionados.split(",").map { it.trim() }
)

interface UsuariosApiService {
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<UsuarioResponse>

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<List<UsuarioResponse>>
}

interface ApiService {
    @GET("api/catalogo/recursos")
    suspend fun obtenerRecursos(): Response<List<RecursoModel>>

    @GET("api/catalogo/recursos/area/{area}")
    suspend fun obtenerRecursosPorArea(@Path("area") area: String): Response<List<RecursoModel>>

    // CRUD completo de Solicitudes
    @GET("api/catalogo/solicitudes")
    suspend fun obtenerSolicitudes(): Response<List<SolicitudDto>>

    @POST("api/catalogo/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @PUT("api/catalogo/solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Path("id") id: String,
        @Body solicitud: SolicitudDto
    ): Response<SolicitudDto>

    @DELETE("api/catalogo/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: String): Response<Unit>
}