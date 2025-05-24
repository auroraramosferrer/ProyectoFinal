package com.example.proyectofinal.model.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.model.Incidencia
import java.text.SimpleDateFormat
import java.util.*

class IncidenciaAdapter(private var incidencias: List<Incidencia>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder>() {

    class IncidenciaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAula: TextView = view.findViewById(R.id.txtAula)
        val txtPrioridad: TextView = view.findViewById(R.id.txtPrioridad)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val rootLayout: View = view.findViewById(R.id.rootItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return IncidenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        val incidencia = incidencias[position]
        Log.d("IncidenciaAdapter", "Fecha: " + incidencia.fecha)
        // Paso 1: Parsear el string de fecha desde el formato ISO UTC
        val formatoUsuario = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaFormateada = incidencia.fecha?.let { formatoUsuario.format(it) } ?: "Fecha desconocida"


        holder.txtFecha.text = "Fecha creación incidencia: $fechaFormateada"

        holder.txtAula.text = "AULA ${incidencia.nombreaula}"
        holder.txtPrioridad.text =
            "Prioridad: ${if (incidencia.prioridad == "3") "Alta" else if (incidencia.prioridad == "2") "Media" else "Baja"}"

        val fondo = when (incidencia.prioridad.lowercase()) {
            "3" -> R.drawable.button_crear_incidencia
            "2" -> R.drawable.button_prioridad_media
            "1" -> R.drawable.button_registrar_equipo
            else -> R.drawable.container_blanco
        }
        holder.rootLayout.setBackgroundResource(fondo)

        holder.rootLayout.setOnClickListener {
            Log.d("IncidenciaAdapter", "Item seleccionado con ID: ${incidencia.id}")
            incidencia.id?.let { it1 -> onItemClick(it1.toInt()) }
        }

    }

    override fun getItemCount(): Int = incidencias.size

    fun actualizarLista(nuevaLista: List<Incidencia>) {
        incidencias = nuevaLista
        notifyDataSetChanged()
    }

}
