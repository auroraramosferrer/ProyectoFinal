package com.example.proyectofinal.ui.consultarIncidencias.resueltas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.proyectofinal.R
import com.example.proyectofinal.model.adapter.IncidenciaAdapter
import com.example.proyectofinal.ui.detallesIncidencias.DetallesIncidenciasFragment
import com.example.proyectofinal.ui.detallesIncidencias.DetallesIncidenciasSharedViewModel

class IncidenciasResueltasFragment : Fragment() {

    private val viewModel: IncidenciasResueltasViewModel by viewModels()
    private val sharedViewModel: DetallesIncidenciasSharedViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IncidenciaAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_incidencias_resueltas, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutResueltas)
        recyclerView = view.findViewById(R.id.recyclerIncidenciasResueltas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncidenciaAdapter(emptyList()) { id ->
            Log.d("IncidenciasResueltasFragment", "Clic en incidencia con ID: $id")
            sharedViewModel.setIncidenciaId(id)

            findNavController().navigate(
                R.id.action_incidencias_to_detalles,
                Bundle().apply {
                    putLong("id", id.toLong())
                    putBoolean("desdeResueltas", true)
                }
            )
        }

        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.cargarIncidenciasResueltas()
        }

        swipeRefreshLayout.isRefreshing = true
        viewModel.cargarIncidenciasResueltas()

        viewModel.incidenciasResueltas.observe(viewLifecycleOwner) { lista ->
            swipeRefreshLayout.isRefreshing = false
            if (lista.isNullOrEmpty()) {
                Log.d("Incidencias", "No se encontraron incidencias resueltas")
            } else {
                adapter.actualizarLista(lista)
            }
        }

        val fabAula = view.findViewById<View>(R.id.btnAulaResueltas)
        val fabFecha = view.findViewById<View>(R.id.btnFechaResueltas)
        val fabPrioridad = view.findViewById<View>(R.id.btnPrioridadResueltas)

        fabAula.setOnClickListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel.setOrden("aula")
        }

        fabFecha.setOnClickListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel.setOrden("fecha")
        }

        fabPrioridad.setOnClickListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel.setOrden("prioridad")
        }
    }
}
