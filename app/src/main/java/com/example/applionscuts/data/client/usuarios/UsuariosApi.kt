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
interface UsuariosApi {

    // Línea 6: GET /posts -> devuelve lista de PostDto
    @GET("all")
    suspend fun getUsuarios(): List<UsuarioDto>

    // Endpoint para obtener un post por ID: GET /posts/{id}
    @GET("{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): UsuarioDto


    @POST("email/{email}")
    suspend fun getUsuarioByEmail(@Path("email") email: String): UsuarioDto

    // Endpoint para actualizar un post existente: PUT /posts/{id}
    @PUT("update/{id}")
    suspend fun update(
        @Path("id") id: Long,
        @Body post: UsuarioDto
    ): UsuarioDto

    // Endpoint para eliminar un post: DELETE /posts/{id}
    @DELETE("delete/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}