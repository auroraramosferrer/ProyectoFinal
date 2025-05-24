package com.example.proyectofinal.ui.editarIncidencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentEditarIncidenciaBinding
import com.example.proyectofinal.model.Incidencia
import kotlinx.coroutines.launch
import java.sql.Date

class EditarIncidenciaFragment : Fragment() {

    private lateinit var binding: FragmentEditarIncidenciaBinding
    private val viewModel: EditarIncidenciaViewModel by viewModels()

    private var incidenciaId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditarIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val descripcion = args?.getString("descripcion") ?: ""
        val prioridad = args?.getString("prioridad") ?: ""
        val estado = args?.getString("estado") ?: ""
        val clase = args?.getString("clase") ?: ""
        val autor = args?.getString("autor") ?: ""
        val solucion = args?.getString("solucion") ?: ""
        incidenciaId = args?.getLong("id") ?: -1

        binding.includeDescripcion.descripcion.setText(descripcion)
        marcarTogglePorTexto(binding.includePrioridad.toggleGroupPrioridad, prioridad)
        marcarTogglePorTexto(binding.includeEstado.toggleGroupEstado, estado)
        marcarTogglePorTexto(binding.includeClase.toggleGroupClase, clase)
        binding.includeAutor.autor.setText(autor)
        binding.includeSolucion.solucion.setText(solucion)

        binding.fabCrearIncidencia.setOnClickListener {
            if (validarCampos()) {
                actualizarIncidencia()
            }
        }
        binding.fabCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun actualizarIncidencia() {
        val descripcion = binding.includeDescripcion.descripcion.text.toString().trim()
        val clase = obtenerTextoToggleSeleccionado(binding.includeClase.toggleGroupClase)
        val autor = binding.includeAutor.autor.text.toString().trim()
        val solucion = binding.includeSolucion.solucion.text.toString().trim()
        val prioridadTexto = obtenerTextoToggleSeleccionado(binding.includePrioridad.toggleGroupPrioridad)
        val estadoTexto = obtenerTextoToggleSeleccionado(binding.includeEstado.toggleGroupEstado)

        val prioridad = when (prioridadTexto.lowercase()) {
            "alta" -> "3"
            "media" -> "2"
            "baja" -> "1"
            else -> {
                Toast.makeText(requireContext(), "Prioridad inválida", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val estado = estadoTexto.lowercase()
        if (estado !in listOf("creada", "procesada", "resuelta")) {
            Toast.makeText(requireContext(), "Estado inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val incidenciaActualizada = Incidencia(
            clase = clase.lowercase(),
            idaula = 0,
            estado = estado,
            autor = autor,
            fecha = Date(System.currentTimeMillis()),
            descripcion = descripcion,
            solucion = solucion,
            prioridad = prioridad,
            idequipo = 0,
            nombreaula = "",
            nombreequipo = ""
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.actualizarIncidencia(
                    incidenciaId.toInt(),
                    incidenciaActualizada
                )
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Incidencia actualizada", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCampos(): Boolean {
        var valido = true
        if (binding.includeAutor.autor.text.isNullOrEmpty()) {
            binding.includeAutor.autor.error = "Campo obligatorio"
            valido = false
        }
        if (obtenerTextoToggleSeleccionado(binding.includePrioridad.toggleGroupPrioridad).isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una prioridad", Toast.LENGTH_SHORT).show()
            valido = false
        }
        if (obtenerTextoToggleSeleccionado(binding.includeEstado.toggleGroupEstado).isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona un estado", Toast.LENGTH_SHORT).show()
            valido = false
        }
        if (obtenerTextoToggleSeleccionado(binding.includeClase.toggleGroupClase).isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una clase", Toast.LENGTH_SHORT).show()
            valido = false
        }

        return valido
    }

    private fun obtenerTextoToggleSeleccionado(group: com.google.android.material.button.MaterialButtonToggleGroup): String {
        val checkedId = group.checkedButtonId
        return if (checkedId != View.NO_ID) {
            group.findViewById<View>(checkedId)?.let { button ->
                (button as? com.google.android.material.button.MaterialButton)?.text?.toString()
            } ?: ""
        } else {
            ""
        }
    }

    private fun marcarTogglePorTexto(
        group: com.google.android.material.button.MaterialButtonToggleGroup,
        valor: String,
    ) {
        val textoNormalizado = when (valor.lowercase()) {
            "3" -> "alta"
            "2" -> "media"
            "1" -> "baja"
            else -> valor.lowercase()
        }

        for (i in 0 until group.childCount) {
            val btn = group.getChildAt(i) as? com.google.android.material.button.MaterialButton
            if (btn?.text.toString().lowercase() == textoNormalizado) {
                if (btn != null) {
                    group.check(btn.id)
                }
                break
            }
        }
    }
}
