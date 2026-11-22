package cl.duoc.aplicacionmoviltherionslabs.network

import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudDto
import retrofit2.Response
import retrofit2.http.*

interface SolicitudesApiService {

    @GET("api/solicitudes")
    suspend fun obtenerTodas(): Response<List<SolicitudDto>>

    @GET("api/solicitudes/usuario/{correo}")
    suspend fun obtenerPorUsuario(@Path("correo") correo: String): Response<List<SolicitudDto>>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): Response<SolicitudDto>

    @PUT("api/solicitudes/{id}")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Body solicitud: SolicitudDto
    ): Response<SolicitudDto>

    @DELETE("api/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Unit>
}