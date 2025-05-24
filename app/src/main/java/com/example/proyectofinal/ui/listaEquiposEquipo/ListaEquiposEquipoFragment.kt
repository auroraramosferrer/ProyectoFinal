package com.example.proyectofinal.ui.listaEquiposEquipo

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentListaEquiposEquipoBinding
import com.example.proyectofinal.model.adapter.EquipoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaEquiposEquipoFragment : Fragment() {


    private lateinit var binding: FragmentListaEquiposEquipoBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var equipoAdapter: EquipoAdapter
    private var aulaId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aulaId = arguments?.getLong("id") ?: 0L
        Log.d("ListaEquiposEquipo", "ID del aula recibido: $aulaId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListaEquiposEquipoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerEquipos
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Añadir listener de swipe para refrescar
        binding.swipeRefresh.setOnRefreshListener {
            fetchEquipos()
        }

        fetchEquipos()
    }


    private fun fetchEquipos() {
        binding.swipeRefresh.isRefreshing = true

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val equiposResponse = RetrofitClient.apiService.getEquiposPorAula(aulaId)
                if (equiposResponse.isNotEmpty()) {
                    Log.d("ListaEquiposEquipo", "Equipos recibidos: $equiposResponse")

                    equipoAdapter = EquipoAdapter(equiposResponse.toMutableList())
                    recyclerView.adapter = equipoAdapter

                    equipoAdapter.setOnItemClickListener { equipo ->
                        Log.d("ListaEquiposEquipo", "Equipo seleccionado: ${equipo.id}")
                        val bundle = Bundle().apply {
                            equipo.id?.let { putLong("id", it) }
                        }
                        findNavController().navigate(
                            R.id.action_listaEquiposEquipoFragment_to_informacionFragment,
                            bundle
                        )
                    }

                } else {
                    Log.e("ListaEquiposEquipo", "No se encontraron equipos para el aula con ID: $aulaId")
                }

            } catch (e: Exception) {
                Log.e("ListaEquiposEquipo", "Error al obtener equipos: ${e.message}")
                e.printStackTrace()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

}
