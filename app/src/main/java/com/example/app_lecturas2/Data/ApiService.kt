package com.example.app_lecturas2.Data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


data class LoginRequest(val user: String, val contraseña: String)
data class LoginResponse(val token: String)

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse_>

    @GET("get_cargas_asignadas")
    suspend fun getCargasAsignadas(@Query("id_operario") idOperario: Int): List<Carga>

    @POST("api/lecturas/enviar") // Endpoint correcto
    suspend fun enviarLecturas(@Body datos: Lectura): Response<Void>



}
