package com.example.app_lecturas2.Ui

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.app_lecturas2.Data.*
import com.example.app_lecturas2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import com.example.app_lecturas2.Data.ApiService



class LecturasActivity : AppCompatActivity() {

    private lateinit var lecturasListView: ListView
    private lateinit var appDao: AppDao
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturas)

        lecturasListView = findViewById(R.id.lecturasListView)
        val db = AppDatabase.getDatabase(this)
        appDao = db.appDao()
        apiService = RetrofitInstance.retrofitInstance.create(ApiService::class.java)

        val idOperario = intent.getIntExtra("idOperario", -1)
        Log.d("LecturasActivity", "ID del operario recibido: $idOperario")

        if (idOperario != -1) {
            obtenerCargasYRegistrarDatos(idOperario)
        } else {
            Toast.makeText(this, "Error: Operario no especificado.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun obtenerCargasYRegistrarDatos(idOperario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cargasAsignadas: List<Carga> = apiService.getCargasAsignadas(idOperario)
                Log.d("LecturasActivity", "Cargas asignadas obtenidas: $cargasAsignadas")

                if (cargasAsignadas.isNotEmpty()) {
                    val lecturasPendientes = appDao.getLecturasNoEnviadas()

                    val direcciones = cargasAsignadas.map { it.toDireccion() }
                    val direccionIds = appDao.insertDirecciones(direcciones)

                    val direccionesConIds = direcciones.mapIndexed { index, direccion ->
                        direccion.apply {
                            id_direccion = direccionIds[index].toInt()
                        }
                    }

                    val clientes = cargasAsignadas.mapIndexed { index, carga ->
                        carga.toCliente(direccionesConIds[index].id_direccion!!)
                    }

                    val clienteIds = appDao.insertClientes(clientes)

                    val clientesConIds = clientes.mapIndexed { index, cliente ->
                        cliente.apply {
                            id_cliente = clienteIds[index].toInt()
                            id_direccion = direccionesConIds[index].id_direccion
                        }
                    }

                    val suministros = cargasAsignadas.mapIndexed { index, carga ->
                        carga.toSuministro(clientesConIds[index].id_cliente!!)
                    }

                    val suministroIds = appDao.insertSuministros(suministros)

                    val suministrosConIds = suministros.mapIndexed { index, suministro ->
                        suministro.apply {
                            id_suministro = suministroIds[index].toInt()
                        }
                    }

                    Log.d("LecturasActivity", "Direcciones después de inserción: $direccionesConIds")
                    Log.d("LecturasActivity", "Clientes después de inserción: $clientesConIds")
                    Log.d("LecturasActivity", "Suministros después de inserción: $suministrosConIds")

                    val datosLecturas = cargasAsignadas.mapIndexed { index, carga ->
                        suministrosConIds[index].apply {
                            clienteNombre = clientesConIds[index].nombre_cliente
                            clienteCodigo = clientesConIds[index].CLICODFAC
                            direccionTexto = "${direccionesConIds[index].urbanizacion}, ${direccionesConIds[index].calle ?: ""} ${direccionesConIds[index].numero ?: ""}"
                            id_suministro = carga.id_suministro
                            id_usuario = carga.id_usuario
                        }
                    }.filterNot { it.id_suministro in lecturasPendientes.map { lectura -> lectura.id_suministro } }

                    withContext(Dispatchers.Main) {
                        val adapter = LecturaAdapter(this@LecturasActivity, datosLecturas, clientesConIds, direccionesConIds)
                        lecturasListView.adapter = adapter

                        lecturasListView.setOnItemClickListener { _, _, position, _ ->
                            val suministro = datosLecturas[position]
                            val cliente = clientesConIds.find { it.id_cliente == suministro.id_cliente }
                            val direccion = direccionesConIds.find { it.id_direccion == cliente?.id_direccion }

                            val intent = Intent(this@LecturasActivity, DetalleLecturaActivity::class.java).apply {
                                putExtra("estado", suministro.ESTADO)
                                putExtra("fecha", suministro.FECINIPREV)
                                putExtra("carga", suministro.CARGA)
                                putExtra("suministro", cliente?.CLICODFAC)
                                putExtra("cliente", cliente?.nombre_cliente)
                                putExtra("medidor", suministro.MEDCODYGO)
                                putExtra("direccion", "${direccion?.urbanizacion} - ${direccion?.calle ?: ""} - ${direccion?.numero ?: "No disponible"}")
                                putParcelableArrayListExtra("lecturas", ArrayList(datosLecturas))
                                putExtra("currentIndex", position)
                            }

                            Log.d("LecturasActivity", "Enviando al Intent: clienteCodigo=${suministro.clienteCodigo}, clienteNombre=${suministro.clienteNombre}, direccionTexto=${suministro.direccionTexto}")
                            Log.d("LecturasActivity", "Enviando lecturas: $datosLecturas")
                            Log.d("LecturasActivity", "Índice seleccionado: $position")

                            startActivity(intent)
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LecturasActivity, "No hay cargas asignadas para el operario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LecturasActivity", "Error al obtener las cargas", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LecturasActivity, "Error al obtener las cargas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun combinarDatos(
        cargas: List<Carga>,
        suministros: List<Suministro>,
        clientes: List<Cliente>,
        direcciones: List<Direccion>
    ): List<Suministro> {
        return suministros.map { suministro ->
            val cliente = clientes.find { it.id_cliente == suministro.id_cliente }
            val direccion = direcciones.find { it.id_direccion == cliente?.id_direccion }

            suministro.apply {
                cliente?.let {
                    this.clienteNombre = it.nombre_cliente
                    this.clienteCodigo = it.CLICODFAC
                }

                direccion?.let {
                    this.direccionTexto = "${it.urbanizacion}, ${it.calle ?: ""}, ${it.numero ?: ""}"
                }
            }
        }
    }

}
