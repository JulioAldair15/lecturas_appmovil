package com.example.app_lecturas2.Data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface AppDao {

    @Query("SELECT * FROM usuario WHERE user = :username AND contraseña = :password LIMIT 1")
    suspend fun getUsuarioByCredentials(username: String, password: String): Usuario?

    @Query("SELECT * FROM suministro")
    fun getAllSuministros(): List<Suministro>

    @Query("SELECT * FROM cliente")
    fun getAllClientes(): List<Cliente>

    @Query("SELECT * FROM direccion")
    fun getAllDirecciones(): List<Direccion>

    @Query("UPDATE suministro SET id_cliente = :clienteId WHERE id_suministro = :suministroId")
    suspend fun updateSuministroWithIds(clienteId: Int, suministroId: Int)

    @Query("UPDATE cliente SET id_direccion = :direccionId WHERE id_cliente = :clienteId")
    suspend fun updateClienteWithIds(direccionId: Int, clienteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuministros(suministros: List<Suministro>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientes(clientes: List<Cliente>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirecciones(direcciones: List<Direccion>): List<Long>

    @Query("DELETE FROM suministro")
    suspend fun DELETESuministros()

    @Query("DELETE FROM cliente")
    suspend fun DELETEClientes()

    @Query("DELETE FROM direccion")
    suspend fun DELETEDirecciones()

    @Query("DELETE FROM foto WHERE id_lectura IN (SELECT id_lectura FROM lectura WHERE id_suministro = :idSuministro)")
    fun deleteFotosBySuministroId(idSuministro: Int)

    @Query("UPDATE lectura SET enviada = 1 WHERE id_suministro = :idSuministro")
    suspend fun updateLecturaAsEnviada(idSuministro: Int)

    @Query("SELECT * FROM lectura WHERE enviada = 0")
    suspend fun getLecturasNoEnviadas(): List<Lectura>

    @Query("DELETE FROM lectura WHERE id_suministro = :idSuministro")
    suspend fun deleteLecturaBySuministroId(idSuministro: Int)


}
