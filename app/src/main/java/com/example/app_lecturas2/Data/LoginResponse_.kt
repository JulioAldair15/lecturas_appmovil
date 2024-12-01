package com.example.app_lecturas2.Data

data class LoginResponse_(
    val id_usuario: Int,
    val token: String,
    val nombres: String,
    val apellidos: String,
    val user: String,
    val tipousu: String
)
