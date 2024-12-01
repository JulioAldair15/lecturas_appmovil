package com.example.app_lecturas2.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lectura",
    foreignKeys = [
        ForeignKey(entity = Suministro::class,
            parentColumns = ["id_suministro"],
            childColumns = ["id_suministro"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class Lectura(
    @PrimaryKey(autoGenerate = true) val id_lectura: Int = 0,
    val LECTURA: Double?,
    val FECLEC: String?,
    val OBS1: String?,
    val OBS2: String?,
    val OBS3: String?,
    val REFUBIME: String?,
    val NEWMED: Double = 0.0,
    val PROMABAJO: Double = 0.0,
    val PROMARRIBA: Double = 0.0,
    val ZONAPELIGROSA: String = "",
    val MODOHOJA: String = "",
    val id_suministro: Int?,
    val id_usuario: Int?,
    var enviada: Boolean = false
)
