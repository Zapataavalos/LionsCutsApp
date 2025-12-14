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
object UsuariosClient {

    // 🔹 IP LOCAL DEL BACKEND USUARIOS
    // ⚠️ Asegúrate que sea la IP de tu PC
    private const val BASE_URL = "http://192.168.100.109:8081/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}