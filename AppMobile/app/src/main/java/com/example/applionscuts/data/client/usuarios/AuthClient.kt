package com.example.applionscuts.data.client.usuarios
// Línea 1: importamos Retrofit para construir el cliente HTTP tipado
import retrofit2.Retrofit
// Línea 2: importamos GsonConverterFactory para convertir JSON <-> objetos
import retrofit2.converter.gson.GsonConverterFactory
// Línea 3: importamos OkHttpClient para configurar timeouts y logging
import okhttp3.OkHttpClient
// Línea 4: importamos HttpLoggingInterceptor para ver requests/responses en Logcat (debug)
import okhttp3.logging.HttpLoggingInterceptor
// Línea 5: objeto singleton que expone Retrofit y la API
object AuthClient {

    private const val BASE_URL = "https://unflavorous-carline-solanaceous.ngrok-free.dev/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
