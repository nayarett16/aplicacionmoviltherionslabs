package cl.duoc.aplicacionmoviltherionslabs.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Rutas enrutadas por therionlabs-gateway
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<List<Any>>

    @GET("api/recursos")
    suspend fun obtenerRecursos(): Response<List<Any>>

    @GET("api/solicitudes")
    suspend fun obtenerSolicitudes(): Response<List<Any>>
}