package com.example.proyectofinal.ui.crearIncidencias

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentCrearIncidenciasBinding
import com.example.proyectofinal.model.Incidencia
import java.sql.Date

class CrearIncidenciasFragment : Fragment() {

    private var _binding: FragmentCrearIncidenciasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CrearIncidenciasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCrearIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idEquipo = arguments?.getLong("id") ?: 0L

        binding.fabCrearIncidencia.setOnClickListener {
            if (validarCampos()) {
                val incidencia = crearIncidencia(idEquipo)

                viewModel.insertarIncidencia(incidencia) { resultado ->
                    if (resultado != null) {
                        Toast.makeText(requireContext(), "Incidencia creada con ID: ${resultado.id}", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), "Error al crear incidencia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.fabCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun crearIncidencia(idEquipo: Long): Incidencia {

        val selectedId = binding.includeClase.toggleGroupClase.checkedButtonId
        Log.e("BOTON", "Botón marcado->" + selectedId)
        val clase = when (selectedId) {
            R.id.btnSoftware -> "software"
            R.id.btnHardware -> "hardware"
            else -> ""
        }

        val prioridad = when (binding.includePrioridad.toggleGroupPrioridad.checkedButtonId) {
            R.id.btnBaja -> "1"
            R.id.btnMedia -> "2"
            R.id.btnAlta -> "3"
            else -> ""  // En caso de que no se haya seleccionado ningún botón
        }

        val estado = when (binding.includeEstado.toggleGroupEstado.checkedButtonId) {
            R.id.btnCreada -> "creada"
            R.id.btnProceso -> "procesada"
            R.id.btnResuelta -> "resuelta"
            else -> ""  // En caso de que no se haya seleccionado ningún botón
        }

        val autor = binding.includeAutor.autorInputLayout.editText?.text.toString().trim()
        val descripcion = binding.includeDescripcion.descripcionInputLayout.editText?.text.toString().trim()
        val solucion = binding.includeSolucion.solucionInputLayout.editText?.text.toString().trim()
        val idaula = arguments?.getInt("idaula") ?: 0
        val fecha = Date(System.currentTimeMillis())

        if (idaula == 0) {
            Toast.makeText(requireContext(), "ID de aula no recibido", Toast.LENGTH_SHORT).show()
        }

        return Incidencia(
            clase = clase,
            idaula = idaula,
            estado = estado,
            autor = autor,
            fecha = fecha,
            descripcion = descripcion,
            solucion = solucion,
            prioridad = prioridad,
            idequipo = idEquipo.toInt(),
            nombreaula = "",
            nombreequipo = ""
        )
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        val autor = binding.includeAutor.autorInputLayout.editText?.text.toString().trim()
        if (autor.isEmpty()) {
            binding.includeAutor.autorInputLayout.error = "El autor es obligatorio"
            isValid = false
        } else {
            binding.includeAutor.autorInputLayout.error = null
        }

        if (binding.includeClase.toggleGroupClase.checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Selecciona una clase", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.includePrioridad.toggleGroupPrioridad.checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Selecciona una prioridad", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.includeEstado.toggleGroupEstado.checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Selecciona un estado", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}