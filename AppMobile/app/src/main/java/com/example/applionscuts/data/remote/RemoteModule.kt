package com.example.applionscuts.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteModule {

    // --- BASE URLs ---
    private const val BASE_URL_CITAS = "https://0k5pc94k-8082.brs.devtunnels.ms/"
    private const val BASE_URL_CATALOGO = "https://0k5pc94k-8083.brs.devtunnels.ms/"
    private const val BASE_URL_CARRITO = "https://0k5pc94k-8084.brs.devtunnels.ms/"

    // --- Retrofit Catálogo ---
    private val retrofitCatalogo by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CATALOGO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Retrofit Citas ---
    private val retrofitCitas by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CITAS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Retrofit Carrito ---
    private val retrofitCarrito by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CARRITO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- APIs ---
    val productoApi: CatalogoApi by lazy {
        retrofitCatalogo.create(CatalogoApi::class.java)
    }

    val citaApi: CitaApi by lazy {
        retrofitCitas.create(CitaApi::class.java)
    }

    val carritoApi: CarritoApi by lazy {
        retrofitCarrito.create(CarritoApi::class.java)
    }
}


