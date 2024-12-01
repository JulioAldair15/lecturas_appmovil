package com.example.app_lecturas2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id_usuario: Int = 0,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val user: String,
    val contraseña: String,
    val tipousu: String,
    val CODUSU: String
)
