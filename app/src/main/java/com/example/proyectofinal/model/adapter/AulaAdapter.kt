package com.example.proyectofinal.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.model.Aula

class AulaAdapter(private var aulas: MutableList<Aula>) : RecyclerView.Adapter<AulaAdapter.AulaViewHolder>() {

    private var onItemClickListener: ((Aula) -> Unit)? = null

    fun setOnItemClickListener(listener: (Aula) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista_equipos_aula_layout, parent, false)
        return AulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        val aula = aulas[position]
        holder.bind(aula)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(aula)
        }
    }

    override fun getItemCount(): Int = aulas.size

    fun updateAulas(newAulas: List<Aula>) {
        aulas.clear()
        aulas.addAll(newAulas)
        notifyDataSetChanged()
    }

    class AulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombreAula)


        fun bind(aula: Aula) {
            nombreTextView.text = "AULA ${aula.nombre}"
        }

    }
}

