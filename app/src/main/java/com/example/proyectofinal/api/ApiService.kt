package com.example.proyectofinal.api


import com.example.proyectofinal.model.Aula
import com.example.proyectofinal.model.Equipo
import com.example.proyectofinal.model.EstadoRequest
import com.example.proyectofinal.model.Incidencia
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("aulas")
    suspend fun getAulas(): List<Aula>

    @POST("aulas")
    suspend fun crearAula(@Body aula: Aula): Aula

    @GET("aulas/nombre/{nombre}")
    suspend fun getAulaPorNombre(@Path("nombre") nombre: String): Aula?

    @GET("aulas/{idaula}/equipos")
    suspend fun getEquiposPorAula(@Path("idaula") aulaId: Long): List<Equipo>

    @GET("equipos")
    suspend fun getEquipos(): List<Equipo>

    @POST("equipos")
    suspend fun crearEquipo(@Body equipo: Equipo): Response<Equipo>

    @GET("equipos/{id}")
    suspend fun getEquipoPorId(@Path("id") id: Long): Equipo

    @DELETE("equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: Long): Response<Void>

    @PUT("equipos/{id}")
    suspend fun actualizarEquipo(
        @Path("id") id: Long,
        @Body equipo: Equipo,
    ): Response<Unit>

    @GET("incidencias")
    suspend fun getIncidencias(): List<Incidencia>

    @GET("incidencias/{id}")
    suspend fun getIncidenciaPorId(@Path("id") id: Int): Incidencia

    @PUT("incidencias/{id}")
    suspend fun actualizarIncidencia(
        @Path("id") id: Int,
        @Body incidencia: Incidencia,
    ): Response<Unit>

    @GET("incidencias/equipo/{id}")
    suspend fun getIncidenciasPorEquipo(@Path("id") idEquipo: Long): List<Incidencia>


    @GET("incidencias/creadas")
    suspend fun getIncidenciasOrdenadas(
        @Query("estados") estados: List<String>,
        @Query("ordenarPor") ordenarPor: String,
    ): Response<List<Incidencia>>

    @GET("/api/incidencias")
    suspend fun getIncidenciasPorEstados(
        @Query("estados") estados: List<String>,
    ): Response<List<Incidencia>>

    @GET("incidencias/filtradas")
    suspend fun getIncidenciasFiltradas(
        @Query("estado") estado: String,
        @Query("orden") orden: String,
    ): Response<List<Incidencia>>

    @POST("incidencias")
    suspend fun crearIncidencia(@Body incidencia: Incidencia): Incidencia

    @DELETE("incidencias/{id}")
    suspend fun eliminarIncidencia(@Path("id") id: Long): Response<Void>

    @PUT("incidencias/{id}/estado")
    suspend fun actualizarEstadoIncidencia(
        @Path("id") id: Long,
        @Body estado: EstadoRequest,
    ): Response<Unit>

    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

}