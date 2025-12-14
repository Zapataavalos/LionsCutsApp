package com.example.applionscuts.data.client.usuarios

// Importamos las anotaciones necesarias para definir endpoints HTTP.
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
// Línea 2: import de nuestro DTO

// Línea 4: interfaz con endpoints del servicio
interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body user: UsuarioDto): Response<UsuarioDto>

    @POST("api/auth/login")
    suspend fun login(@Body user: UsuarioDto): Response<UsuarioDto>
}
