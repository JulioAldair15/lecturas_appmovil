package com.example.app_lecturas2.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.app_lecturas2.Data.AppDatabase
import com.example.app_lecturas2.Data.AppDao
import com.example.app_lecturas2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var appDao: AppDao
    private lateinit var inicialText: TextView
    private lateinit var enCursoText: TextView
    private lateinit var enEjecucionText: TextView
    private lateinit var rechazadasText: TextView
    private lateinit var registerButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicialText = findViewById(R.id.inicialText)
        enCursoText = findViewById(R.id.enCursoText)
        enEjecucionText = findViewById(R.id.enEjecucionText)
        rechazadasText = findViewById(R.id.rechazadasText)
        registerButton = findViewById(R.id.registerButton)

        val db = AppDatabase.getDatabase(this)
        appDao = db.appDao()

        val idOperario = intent.getIntExtra("idOperario", -1)
        if (idOperario != -1) {
            Log.d("MainActivity", "ID del operario recibido: $idOperario")
        } else {
            Toast.makeText(this, "Error: Operario no especificado", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Operario no especificado en el Intent")
            finish()
        }

        cargarResumenEstados()

            registerButton.setOnClickListener {
                val intent = Intent(this, LecturasActivity::class.java).apply {
                    putExtra("idOperario", idOperario)
                }
                startActivity(intent)
            }
        }


        private fun cargarResumenEstados() {
        CoroutineScope(Dispatchers.IO).launch {
            val suministros = appDao.getAllSuministros()
            val initialCount = suministros.count { it.ESTADO == "Inicial" }
            val inProgressCount = suministros.count { it.ESTADO == "En Curso" }
            val completedCount = suministros.count { it.ESTADO == "En Ejecución" }
            val rejectedCount = suministros.count { it.ESTADO == "Rechazadas" }

            withContext(Dispatchers.Main) {
                inicialText.text = "$initialCount Inicial"
                enCursoText.text = "$inProgressCount en Curso"
                enEjecucionText.text = "$completedCount en Ejecución"
                rechazadasText.text = "$rejectedCount Rechazadas"
            }
        }
    }
}
