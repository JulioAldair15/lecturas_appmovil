package com.example.app_lecturas2.Data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "suministro")
data class Suministro(
    @PrimaryKey(autoGenerate = true) var id_suministro: Int = 0,
    var OK: String? = null,
    var MEDCODYGO: String? = null,
    var CICLO: String? = null,
    var CARGA: String? = null,
    var ORDEN: String? = null,
    var TIPO: String? = null,
    var PROMEDIO: String? = null,
    var LECTANT: String? = null,
    var OBSANT: String? = null,
    var FECINIPREV: String? = null,
    var FECFINPREV: String? = null,
    var MES: String? = null,
    var ESTADO: String? = null,
    var id_cliente: Int? = null,
    var id_usuario: Int? = null,

    var clienteNombre: String? = "No disponible",
    var clienteCodigo: String? = "No disponible",
    var direccionTexto: String? = "No disponible"
) : Parcelable
