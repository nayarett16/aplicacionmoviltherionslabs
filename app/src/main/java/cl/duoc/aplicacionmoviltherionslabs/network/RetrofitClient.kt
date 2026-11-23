package cl.duoc.aplicacionmoviltherionslabs.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val GATEWAY_URL = "https://therionlabs-gateway.onrender.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GATEWAY_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente para usuarios-service (/api/usuarios)
    val usuariosApi: UsuariosApiService by lazy {
        retrofit.create(UsuariosApiService::class.java)
    }

    // Cliente para catalogo-service (/api/catalogo/...)
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Cliente para solicitudes-service (/api/solicitudes)
    val solicitudesApi: SolicitudesApiService by lazy {
        retrofit.create(SolicitudesApiService::class.java)
    }
}