package cl.duoc.aplicacionmoviltherionslabs.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 apunta al localhost de tu computador desde el emulador de Android
    private const val URL_USUARIOS = "http://10.0.2.2:8081/"
    private const val URL_CATALOGO = "http://10.0.2.2:8082/"

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

    // Cliente para usuarios-service (8081)
    val usuariosApi: UsuariosApiService by lazy {
        retrofitUsuarios.create(UsuariosApiService::class.java)
    }

    // Cliente para catalogo-service (8082) - usado por RepositorioRecursos
    val apiService: ApiService by lazy {
        retrofitCatalogo.create(ApiService::class.java)
    }
}