package cl.duoc.aplicacionmoviltherionslabs.network

import cl.duoc.aplicacionmoviltherionslabs.model.RecursoModel
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudActividad
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import cl.duoc.aplicacionmoviltherionslabs.model.SolicitudRequestDto

// Modelos para el Login contra usuarios-service (8081)
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

// Interfaz para el Microservicio de Usuarios (Puerto 8081)
interface UsuariosApiService {
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<UsuarioResponse>

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<List<UsuarioResponse>>
}

// Interfaz para el Microservicio de Catálogo (Puerto 8082)
interface ApiService {
    @GET("api/catalogo/recursos")
    suspend fun obtenerRecursos(): Response<List<RecursoModel>>

    @GET("api/catalogo/recursos/area/{area}")
    suspend fun obtenerRecursosPorArea(@Path("area") area: String): Response<List<RecursoModel>>

    @GET("api/catalogo/solicitudes")
    suspend fun obtenerSolicitudes(): Response<List<SolicitudActividad>>

    @POST("api/catalogo/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudRequestDto): Response<Any>
}