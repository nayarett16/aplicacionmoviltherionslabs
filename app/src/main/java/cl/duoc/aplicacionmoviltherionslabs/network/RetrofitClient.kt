package cl.duoc.aplicacionmoviltherionslabs.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
        private const val BASE_URL = "https://therionlabs-gateway.onrender.com/"

        val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }
    }
