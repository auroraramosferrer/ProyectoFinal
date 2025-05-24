package com.example.proyectofinal.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemListaEquiposEquipoLayoutBinding
import com.example.proyectofinal.model.Equipo

class EquipoAdapter(private val equipos: MutableList<Equipo>) :
    RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder>() {

    private var onItemClickListener: ((Equipo) -> Unit)? = null

    fun setOnItemClickListener(listener: (Equipo) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val binding = ItemListaEquiposEquipoLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return EquipoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        val equipo = equipos[position]
        holder.bind(equipo)
    }

    override fun getItemCount() = equipos.size

    inner class EquipoViewHolder(private val binding: ItemListaEquiposEquipoLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(equipo: Equipo) {
            binding.textViewNombreEquipo.text = "EQUIPO ${equipo.codigo}"

            // Manejar clic en el ítem completo
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(equipo)
            }

            // También puedes manejar clics individuales si quieres
            // Pulsar el botón provoca la navegación
//            binding.imageButtonListaEquipos.setOnClickListener {
//                onItemClickListener?.invoke(equipo)
//            }

        }
    }

    fun updateEquipos(newEquipos: List<Equipo>) {
        equipos.clear()
        equipos.addAll(newEquipos)
        notifyDataSetChanged()
    }
}

