package com.example.app_lecturas2.Data
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "foto",
    foreignKeys = [ForeignKey(entity = Lectura::class,
        parentColumns = ["id_lectura"],
        childColumns = ["id_lectura"],
        onDelete = ForeignKey.CASCADE)]
)
data class Foto(
    @PrimaryKey(autoGenerate = true) val id_foto: Int = 0,
    val ruta: String,
    val descripcion: String?,
    val fecha_captura: String?,
    val hora_captura: String?,
    val latitud: Double?,
    val longitud: Double?,
    val id_lectura: Int?,
    val foto_base64: String?
)
