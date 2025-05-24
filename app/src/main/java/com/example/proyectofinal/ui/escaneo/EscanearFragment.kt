package com.example.proyectofinal.ui.escaneo

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EscanearFragment : Fragment() {

    companion object {
        fun newInstance() = EscanearFragment()
    }

    private val viewModel: EscanearViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_escanear, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val inputEscaneo = view.findViewById<TextInputEditText>(R.id.escaneo)

        inputEscaneo.requestFocus() // para que el escáner escriba ahí

        inputEscaneo.setOnEditorActionListener { _, actionId, event ->
            val isEnter = actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)

            if (isEnter) {
                val idTexto = inputEscaneo.text.toString().trim()
                if (idTexto.isNotEmpty()) {
                    val id = idTexto.toLongOrNull()
                    if (id != null) {
                        buscarEquipoPorId(id)
                        inputEscaneo.text?.clear()
                    } else {
                        Toast.makeText(requireContext(), "ID no válido", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            } else {
                false
            }
        }
    }

    private fun buscarEquipoPorId(id: Long) {
        lifecycleScope.launch {
            try {
                val equipo = RetrofitClient.apiService.getEquipoPorId(id)
                val bundle = Bundle().apply {
                    equipo.id?.let { putLong("id", it) }

                    // Añade más campos si los necesitas
                }
                findNavController().navigate(R.id.action_escaneo_to_informacion, bundle)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Equipo no encontrado o error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }


}