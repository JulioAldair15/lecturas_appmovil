package com.example.app_lecturas2.Ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.app_lecturas2.Data.Suministro
import com.example.app_lecturas2.R
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.EditText
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.app_lecturas2.Data.Lectura
import com.example.app_lecturas2.Data.LecturaRequest
import com.example.app_lecturas2.Data.Foto
import com.example.app_lecturas2.Data.AppDatabase
import com.example.app_lecturas2.Data.AppDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.withContext
import android.util.Base64
import java.io.ByteArrayOutputStream


class DetalleLecturaActivity : AppCompatActivity() {

    private lateinit var lecturas: List<Suministro>
    private lateinit var appDao: AppDao
    private var currentIndex: Int = 0
    private lateinit var lecturaInput: EditText
    private lateinit var detallesInput: EditText
    private lateinit var observationText1: TextView
    private lateinit var observationText2: TextView
    private lateinit var observationText3: TextView
    private lateinit var photoPreview1: ImageView
    private lateinit var photoPreview2: ImageView
    private lateinit var photoPreview3: ImageView
    private lateinit var deleteIcon1: ImageView
    private lateinit var deleteIcon2: ImageView
    private lateinit var deleteIcon3: ImageView
    private val fotos: MutableList<Bitmap> = mutableListOf()


    private val fotosBase64: MutableList<String> = mutableListOf()

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    val fotoBase64 = convertBitmapToBase64(it)
                    when (fotos.size) {
                        0 -> {
                            photoPreview1.setImageBitmap(it)
                            deleteIcon1.setOnClickListener {
                                fotos.removeAt(0)
                                photoPreview1.setImageResource(R.drawable.ic_photo_placeholder)
                            }
                        }
                        1 -> {
                            photoPreview2.setImageBitmap(it)
                            deleteIcon2.setOnClickListener {
                                fotos.removeAt(1)
                                photoPreview2.setImageResource(R.drawable.ic_photo_placeholder)
                            }
                        }
                        2 -> {
                            photoPreview3.setImageBitmap(it)
                            deleteIcon3.setOnClickListener {
                                fotos.removeAt(2)
                                photoPreview3.setImageResource(R.drawable.ic_photo_placeholder)
                            }
                        }
                    }
                    fotos.add(it)
                    fotosBase64.add(fotoBase64)
                }
            }
        }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_lectura)

        deleteIcon1 = findViewById(R.id.deleteIcon1)
        deleteIcon2 = findViewById(R.id.deleteIcon2)
        deleteIcon3 = findViewById(R.id.deleteIcon3)

        val db = AppDatabase.getDatabase(this)
        appDao = db.appDao()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lecturas = intent.getParcelableArrayListExtra("lecturas", Suministro::class.java) ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            lecturas = intent.getParcelableArrayListExtra("lecturas") ?: listOf()
        }
        lecturas = intent.getParcelableArrayListExtra("lecturas", Suministro::class.java) ?: listOf()
        currentIndex = intent.getIntExtra("currentIndex", 0)

        if (lecturas.isEmpty()) {
            Log.e("DetalleLecturaActivity", "Error: La lista de lecturas está vacía")
            Toast.makeText(this, "No hay lecturas disponibles", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (currentIndex !in lecturas.indices) {
            Log.e("DetalleLecturaActivity", "Error: Índice actual ($currentIndex) fuera de rango")
            Toast.makeText(this, "Índice inválido de lectura", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarLectura(lecturas[currentIndex])

        lecturaInput = findViewById(R.id.lecturaInput)
        detallesInput = findViewById(R.id.detallesInput)
        observationText1 = findViewById(R.id.observationText1)
        observationText2 = findViewById(R.id.observationText2)
        observationText3 = findViewById(R.id.observationText3)
        photoPreview1 = findViewById(R.id.photoPreview1)
        photoPreview2 = findViewById(R.id.photoPreview2)
        photoPreview3 = findViewById(R.id.photoPreview3)

        val cameraIcon = findViewById<ImageView>(R.id.cameraIcon)
        cameraIcon.setOnClickListener { abrirCamara() }

        configurarObservacion1()
        configurarBotones()
    }

    private fun mostrarLectura(lectura: Suministro) {

        Log.d("DetalleLecturaActivity", "Mostrando lectura: $lectura")

        val suministro = lectura.clienteCodigo ?: "No disponible"
        val cliente = lectura.clienteNombre ?: "No disponible"
        val medidor = lectura.MEDCODYGO ?: "No disponible"
        val direccion = lectura.direccionTexto ?: "No disponible"

        val infoTextView = findViewById<TextView>(R.id.infoTextView)
        val infoText = """
            Suministro: $suministro
            Cliente: $cliente
            N° de Medidor: $medidor
            Dirección: $direccion
        """.trimIndent()
        infoTextView.text = infoText
    }

    private fun abrirCamara() {
        if (fotos.size < 3) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Solo puedes tomar hasta 3 fotos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarObservacion1() {
        val observationText1 = findViewById<TextView>(R.id.observationText1)
        val arrowDown1 = findViewById<ImageView>(R.id.arrowDown1)

        val opcionesObservacion1 = listOf(
            "00 - SIN OBSERVACION",
            "01 - MEDIDOR INVERTIDO",
            "02 - IMPOSIBILIDAD CIRCUNSTANCIAL DE LECTURA",
            "03 - FUGA DE AGUA EN CAJA DE REGISTRO",
            "04 - MEDIDOR EN EL INTERIOR DEL PREDIO",
            "05 - SIN MEDIDOR (CONEXIÓN DIRECTA)",
            "06 - MEDIDOR CON VANDALISMO",
            "07 - CONEXIÓN INUBICABLE",
            "08 - LUNETA DE MEDIDOR ROTA",
            "09 - EXISTENCIA DE BY-PASS",
            "10 - MEDIDOR ENTERRADO",
            "11 - MEDIDOR PARALIZADO",
            "12 - NUMERO DE MEDIDOR INCORRECTO",
            "13 - DIRECCION ERRADA",
            "14 - MEDIDOR MANIPULADO",
            "15 - CONEXIÓN CORTADA",
            "16 - REAPERTURA CLANDESTINA",
            "17 - CAJA DE REGISTRO SELLADA",
            "18 - MARCO DE CAJA DE REGISTRO DETERIORADA",
            "19 - CAJA DE REGISTRO SIN TAPA O TAPA ROTA",
            "20 - CAJA DE REGISTRO DETERIORADA",
            "23 - PREDIO DESHABITADO",
            "24 - TERRENO BALDIO",
            "25 - MEDIDOR SIN ANCLAJE",
            "26 - CAJA ENTERRADA SIN VEREDA",
            "91 - LUNETA DE MEDIDOR EMPAÑADA - ALTA",
            "92 - LUNETA DE MEDIDOR EMPAÑADA - MEDIA",
            "93 - LUNETA DE MEDIDOR EMPAÑADA - BAJA"
        )

        val clickListener = View.OnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            opcionesObservacion1.forEachIndexed { index, opcion ->
                popupMenu.menu.add(0, index, 0, opcion)
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                val seleccion = opcionesObservacion1[item.itemId]
                observationText1.text = seleccion
                true
            }

            popupMenu.show()
        }

        observationText1.setOnClickListener(clickListener)
        arrowDown1.setOnClickListener(clickListener)
    }

    private fun enviarDatosAlSistemaWeb(callback: (Boolean) -> Unit) {
        val lecturaTexto = lecturaInput.text.toString()
        if (lecturaTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese una lectura válida", Toast.LENGTH_SHORT).show()
            callback(false)
            return
        }

        val detalles = detallesInput.text.toString()
        val obs1 = observationText1.text.toString()
        val obs2 = observationText2.text.toString()
        val obs3 = observationText3.text.toString()
        val fechaActual = obtenerFechaActual()

        val currentLectura = lecturas[currentIndex]
        val fotosSerializadas = fotosBase64.mapIndexed { index, fotoBase64 ->
            Foto(
                ruta = "photo_${index + 1}.jpg",
                descripcion = "Foto ${index + 1}",
                fecha_captura = fechaActual,
                hora_captura = obtenerHoraActual(),
                latitud = null,
                longitud = null,
                id_lectura = null,
                foto_base64 = fotoBase64
            )
        }

        val lecturaData = Lectura(
            LECTURA = lecturaTexto.toDouble(),
            FECLEC = fechaActual,
            OBS1 = obs1,
            OBS2 = obs2,
            OBS3 = obs3,
            REFUBIME = detalles,
            id_suministro = currentLectura.id_suministro,
            id_usuario = currentLectura.id_usuario,
            enviada = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://54.94.89.152")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                val lecturaRequest = LecturaRequest(
                    lectura = lecturaData,
                    fotos = fotosSerializadas
                )

                val response = apiService.enviarLectura(lecturaRequest)
                if (response.isSuccessful) {
                    Log.d("DetalleLecturaActivity", "Datos enviados correctamente")
                    appDao.updateLecturaAsEnviada(currentLectura.id_suministro)
                    eliminarDatosDeLaBaseDeDatosLocal(currentLectura)

                    withContext(Dispatchers.Main) {
                        limpiarPantalla()
                        Toast.makeText(this@DetalleLecturaActivity, "Datos enviados con éxito", Toast.LENGTH_SHORT).show()
                    }
                    callback(true)
                } else {
                    Log.e("DetalleLecturaActivity", "Error al enviar datos: ${response.code()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetalleLecturaActivity, "Error al enviar datos: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    callback(false)
                }
            } catch (e: Exception) {
                Log.e("DetalleLecturaActivity", "Error al conectar con el servidor: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetalleLecturaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
                callback(true)
            }
        }

    }

    private suspend fun eliminarDatosDeLaBaseDeDatosLocal(lectura: Suministro) {
        appDao.updateLecturaAsEnviada(lectura.id_suministro)

        appDao.deleteFotosBySuministroId(lectura.id_suministro)
        Log.d("DetalleLecturaActivity", "Fotos eliminadas para el suministro: ${lectura.id_suministro}")

        appDao.deleteLecturaBySuministroId(lectura.id_suministro)
        Log.d("DetalleLecturaActivity", "Lectura eliminada para el suministro: ${lectura.id_suministro}")

        Log.d("DetalleLecturaActivity", "Datos eliminados de la base de datos local para el suministro: ${lectura.id_suministro}")
    }

    private fun limpiarPantalla() {
        lecturaInput.text.clear()
        detallesInput.text.clear()
        observationText1.text = "Seleccionar observación"
        observationText2.text = "Seleccionar observación"
        observationText3.text = "Seleccionar observación"

        photoPreview1.setImageResource(R.drawable.ic_photo_placeholder)
        photoPreview2.setImageResource(R.drawable.ic_photo_placeholder)
        photoPreview3.setImageResource(R.drawable.ic_photo_placeholder)

        fotos.clear()
    }




    private fun obtenerUsuarioActual(): Int {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getInt("id_usuario", -1)
    }


    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun obtenerHoraActual(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun configurarBotones() {
        val previousButton = findViewById<ImageButton>(R.id.prevButton)
        previousButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                mostrarLectura(lecturas[currentIndex])
            } else {
                Toast.makeText(this, "No hay lecturas anteriores", Toast.LENGTH_SHORT).show()
            }
        }

        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        nextButton.setOnClickListener {
            if (currentIndex < lecturas.size - 1) {
                currentIndex++
                mostrarLectura(lecturas[currentIndex])
            } else {
                Toast.makeText(this, "No hay más lecturas", Toast.LENGTH_SHORT).show()
            }
        }

        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            val lecturaTexto = lecturaInput.text.toString()
            val observacion1 = observationText1.text.toString()
            val foto1 = photoPreview1.drawable

            if (lecturaTexto.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, "Debe ingresar una lectura válida.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            if (observacion1.isEmpty() || observacion1 == "") {
                runOnUiThread {
                    Toast.makeText(this, "Debe seleccionar una observación válida.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            if (foto1 == null || foto1.constantState == resources.getDrawable(R.drawable.ic_photo_placeholder).constantState) {
                runOnUiThread {
                    Toast.makeText(this, "Debe agregar al menos una foto.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            enviarDatosAlSistemaWeb { exito ->
                runOnUiThread {
                    if (exito) {
                        if (currentIndex < lecturas.size - 1) {
                            currentIndex++
                            mostrarLectura(lecturas[currentIndex])
                            limpiarPantalla()
                        } else {
                            Toast.makeText(this, "No hay más lecturas disponibles", Toast.LENGTH_SHORT).show()
                            sendButton.isEnabled = false
                        }
                    } else {
                        Toast.makeText(this, "Error al enviar datos, intenta nuevamente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}

interface ApiService {
    @POST("/api/lecturas/enviar")
    suspend fun enviarLectura(
        @Body request: LecturaRequest
    ): Response<Unit>
}
