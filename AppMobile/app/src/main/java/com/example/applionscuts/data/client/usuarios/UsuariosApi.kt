package com.example.applionscuts.data.client.usuarios
// Importamos las anotaciones necesarias para definir endpoints HTTP.
import com.example.applionscuts.data.client.usuarios.dto.UsuarioDto
import com.example.applionscuts.data.client.usuarios.dto.UsuarioUpdateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
// Línea 2: import de nuestro DTO

// Línea 4: interfaz con endpoints del servicio
interface UsuariosApi {

    @GET("api/usuarios/all")
    suspend fun getUsuarios(): List<UsuarioDto>

    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): UsuarioDto

    @POST("api/usuarios/email/{email}")
    suspend fun getUsuarioByEmail(@Path("email") email: String): UsuarioDto

    @PUT("api/usuarios/update/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body user: UsuarioUpdateDto
    ): UsuarioDto



    @DELETE("api/usuarios/delete/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}
