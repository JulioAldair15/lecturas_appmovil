// Clase Carga.kt
package com.example.app_lecturas2.Data

data class Carga(
    val carga: String,
    val usuario: String,
    val fecha: String,
    val estado: String,
    val suministro: String,
    val medidor: String,
    val direccion: String,
    val idOperario: Int,
    val id_suministro: Int,
    val id_usuario: Int
) {
    fun toSuministro(idCliente: Int): Suministro {
        return Suministro(
            id_suministro = this.id_suministro,
            OK = null,
            MEDCODYGO = this.medidor,
            CICLO = null,
            CARGA = this.carga,
            ORDEN = null,
            TIPO = null,
            PROMEDIO = null,
            LECTANT = null,
            OBSANT = null,
            FECINIPREV = this.fecha,
            FECFINPREV = null,
            MES = null,
            ESTADO = this.estado,
            id_cliente = idCliente,
            id_usuario = this.id_usuario
        )
    }

    fun toCliente(idDireccion: Int): Cliente {
        return Cliente(
            id_cliente = 0,
            CLICODFAC = this.suministro,
            nombre_cliente = this.usuario,
            id_direccion = idDireccion
        )
    }

    fun toDireccion(): Direccion {
        val partesDireccion = this.direccion.split(",").map { it.trim() }
        return Direccion(
            id_direccion = 0,
            urbanizacion = partesDireccion.getOrNull(0) ?: "Desconocido",
            calle = partesDireccion.getOrNull(1),
            numero = partesDireccion.getOrNull(2)
        )
    }


}
