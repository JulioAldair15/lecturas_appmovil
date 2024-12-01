package com.example.app_lecturas2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "cliente",
    foreignKeys = [ForeignKey(
        entity = Direccion::class,
        parentColumns = ["id_direccion"],
        childColumns = ["id_direccion"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Cliente(
    @PrimaryKey(autoGenerate = true) var id_cliente: Int = 0,
    var CLICODFAC: String,
    var nombre_cliente: String?,
    var id_direccion: Int?
)
