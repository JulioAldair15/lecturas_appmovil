package com.example.app_lecturas2.Data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "direccion")
data class Direccion(
    @PrimaryKey(autoGenerate = true) var id_direccion: Int = 0,
    val urbanizacion: String?,
    val calle: String?,
    val numero: String?
)
