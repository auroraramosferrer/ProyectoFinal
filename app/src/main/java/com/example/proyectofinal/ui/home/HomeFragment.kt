package com.example.proyectofinal.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        binding.btnRegistrarEquipo.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_home_to_registro)
        }
        binding.btnEscanearEquipo.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_home_to_escaneo)
        }
        binding.btnConsultarIncidencia.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_home_to_consultar_incidencias)
        }
        binding.btnListaEquipos.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_home_to_lista_equipos_aulas)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}