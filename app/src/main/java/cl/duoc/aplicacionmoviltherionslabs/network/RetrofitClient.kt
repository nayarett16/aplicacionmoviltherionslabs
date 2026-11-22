package cl.duoc.aplicacionmoviltherionslabs.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val URL_USUARIOS = "http://192.168.8.144:8081/"
    private const val URL_CATALOGO = "http://192.168.8.144:8082/"
    private const val URL_SOLICITUDES = "http://192.168.8.144:8083/"

    private val retrofitUsuarios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_USUARIOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitCatalogo: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_CATALOGO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitSolicitudes: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_SOLICITUDES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente para usuarios-service (8081)
    val usuariosApi: UsuariosApiService by lazy {
        retrofitUsuarios.create(UsuariosApiService::class.java)
    }

    // Cliente para catalogo-service (8082) - usado por RepositorioRecursos
    val apiService: ApiService by lazy {
        retrofitCatalogo.create(ApiService::class.java)
    }

    // Cliente para solicitudes-service (8083)
    val solicitudesApi: SolicitudesApiService by lazy {
        retrofitSolicitudes.create(SolicitudesApiService::class.java)
    }
}