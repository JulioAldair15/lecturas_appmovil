package com.example.app_lecturas2.Ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.app_lecturas2.R
import com.example.app_lecturas2.Data.Suministro
import com.example.app_lecturas2.Data.Cliente
import com.example.app_lecturas2.Data.Direccion
import com.example.app_lecturas2.Data.Carga
import java.text.SimpleDateFormat
import java.util.Locale
import android.text.SpannableString
import android.text.Spannable
import android.text.style.StyleSpan
import android.graphics.Typeface


fun formatFecha(fecha: String): String {
    val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("d 'de' MMMM 'del' yyyy", Locale("es", "ES"))

    return try {
        val date = inputFormat.parse(fecha)
        outputFormat.format(date)
    } catch (e: Exception) {
        fecha
    }
}

class LecturaAdapter(
    context: Context,
    private val datosLecturas: List<Suministro>,
    private val clientes: List<Cliente>,
    private val direcciones: List<Direccion>
) : ArrayAdapter<Suministro>(context, 0, datosLecturas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_lectura, parent, false)

        Log.d("LecturaAdapter", "Mostrando datos en la posición $position: ${datosLecturas[position]}")

        val suministro = datosLecturas[position]
        //val cliente = clientes[position]
        //val direccion = direcciones[position]
        Log.d("Lecturas", "${suministro.id_cliente}")

        val cliente = clientes.find { it.id_cliente == suministro.id_cliente }
        val direccion = direcciones.find { it.id_direccion == cliente?.id_direccion }

        Log.d("Lecturas", "${cliente}")

        val estadoTextView = view.findViewById<TextView>(R.id.estadoText)
        val fechaTextView = view.findViewById<TextView>(R.id.fechaText)
        val cargaTextView = view.findViewById<TextView>(R.id.cargaText)
        val suministroTextView = view.findViewById<TextView>(R.id.suministroText)
        val usuarioTextView = view.findViewById<TextView>(R.id.usuarioText)
        val medidorTextView = view.findViewById<TextView>(R.id.medidorText)
        val ubicacionTextView = view.findViewById<TextView>(R.id.ubicacionText)

        Log.d("cliente", "${cliente?.CLICODFAC}")
        estadoTextView.text = formatLabel("ESTADO: ", suministro.ESTADO ?: "No disponible")
        fechaTextView.text = formatLabel("FECHA: ", formatFecha(suministro.FECINIPREV ?: "No disponible"))
        cargaTextView.text = formatLabel("CARGA: ", suministro.CARGA ?: "No disponible")
        suministroTextView.text = formatLabel("SUMINISTRO: ", cliente?.CLICODFAC ?: "No disponible")
        usuarioTextView.text = formatLabel("CLIENTE: ", cliente?.nombre_cliente ?: "No disponible")
        medidorTextView.text = formatLabel("N° DE MEDIDOR: ", suministro.MEDCODYGO ?: "No disponible")
        ubicacionTextView.text = formatLabel("DIRECCION: ", "${direccion?.urbanizacion} - ${direccion?.calle ?: ""} - ${direccion?.numero ?: "No disponible"}")


        return view
    }

    private fun formatLabel(label: String, value: String): SpannableString {
        val spannable = SpannableString("$label$value")
        spannable.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}