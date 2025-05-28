package com.example.proyectofinal.ui.detallesIncidencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentDetallesIncidenciasBinding
import kotlinx.coroutines.launch
import com.example.proyectofinal.model.EstadoRequest
import com.example.proyectofinal.ui.detasllesIncidencias.DetallesIncidenciasSharedViewModel

class DetallesIncidenciasFragment : Fragment() {

    private lateinit var binding: FragmentDetallesIncidenciasBinding
    private val sharedViewModel: DetallesIncidenciasSharedViewModel by activityViewModels()
    private var id: Long = -1
    private var idequipo: Int = -1
    private var idaula: Int = -1

    companion object {
        fun newInstance() = DetallesIncidenciasFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetallesIncidenciasBinding.inflate(inflater, container, false)

        val desdeResueltas = arguments?.getBoolean("desdeResueltas", false) ?: false
        if (desdeResueltas) {
            binding.fabCompletarIncidencia.visibility = View.GONE
        }

        sharedViewModel.incidenciaId.observe(viewLifecycleOwner) { incidenciaId ->
            if (incidenciaId != -1) {
                id = incidenciaId.toLong()
                cargarDetallesIncidencia(id)
            }
        }

        binding.fabEliminarIncidencia.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás segur@ de que deseas eliminar esta incidencia?")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.apiService.eliminarIncidencia(id)
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "Incidencia eliminada", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "Error al eliminar la incidencia", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "Error de red al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    Toast.makeText(requireContext(), "Cancelando operación", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .show()
        }

        binding.fabCompletarIncidencia.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Marcar como resuelta")
                .setMessage("¿Quieres marcar esta incidencia como resuelta?")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            val respuesta = RetrofitClient.apiService.actualizarEstadoIncidencia(
                                id,
                                EstadoRequest("resuelta")
                            )
                            if (respuesta.isSuccessful) {
                                Toast.makeText(requireContext(), "Incidencia marcada como resuelta", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "Error al actualizar el estado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "Error de red al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.fabEditarIncidencia.setOnClickListener {

            val bundle = Bundle().apply {
                putLong("id", id)
                putString("descripcion", binding.textViewTextoDescripcion.text.toString())
                putString("prioridad", binding.textViewTextoPrioridad.text.toString())
                putString("estado", binding.textViewTextoEstado.text.toString())
                putString("clase", binding.textViewTextoClase.text.toString())
                putString("autor", binding.textViewNombreAutor.text.toString())
                putString("solucion", binding.textViewTextoSolucion.text.toString())
                putLong("idequipo", idequipo.toLong())
                putLong("idaula", idaula.toLong())

            }

            findNavController().navigate(
                R.id.action_detalles_to_editar_incidencia,
                bundle
            )
        }

        return binding.root
    }

    private fun cargarDetallesIncidencia(id: Long) {
        lifecycleScope.launch {
            try {
                val incidencia = RetrofitClient.apiService.getIncidenciaPorId(id.toInt())


                incidencia?.let {
                    idequipo = it.idequipo
                    idaula = it.idaula

                    binding.textViewEquipo.text = "EQUIPO ${it.nombreequipo}"
                    binding.textViewTextoEstado.text = when (it.estado.lowercase()) {
                        "creada" -> "Creada"
                        "procesada" -> "Procesada"
                        "resuelta" -> "Resuelta"
                        else -> "Desconocido"
                    }

                    val fondoEstado = when (it.estado.lowercase()) {
                        "creada" -> R.drawable.bg_estado_creado
                        "procesada" -> R.drawable.bg_estado_procesado
                        "resuelta" -> R.drawable.bg_estado_resuelto
                        else -> R.drawable.bg_estado_default
                    }
                    binding.viewContenedorEstado.setBackgroundResource(fondoEstado)

                    val fondoPrioridad = when (it.prioridad.lowercase()) {
                        "1" -> R.drawable.bg_prioridad_baja
                        "2" -> R.drawable.bg_prioridad_media
                        "3" -> R.drawable.bg_prioridad_alta
                        else -> R.drawable.bg_estado_default
                    }
                    binding.viewContenedorPrioridad.setBackgroundResource(fondoPrioridad)

                    val fondoClase = when (it.clase.lowercase()) {
                        "software" -> R.drawable.bg_clase_software
                        "hardware" -> R.drawable.bg_clase_hardware
                        else -> R.drawable.bg_estado_default
                    }
                    binding.viewContenedorClase.setBackgroundResource(fondoClase)

                    binding.textViewTextoPrioridad.text = when (it.prioridad.lowercase()) {
                        "1" -> "Baja"
                        "2" -> "Media"
                        else -> "Alta"
                    }

                    binding.textViewTextoClase.text = if (it.clase == "software") "Software" else "Hardware"
                    binding.textViewTextoDescripcion.text = it.descripcion
                    binding.textViewTextoSolucion.text = it.solucion
                    binding.textViewNombreAutor.text = it.autor
                    binding.textViewAula.text = "AULA ${it.nombreaula}"


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


