package com.example.proyectofinal.ui.consultarIncidencias.activas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.model.adapter.IncidenciaAdapter
import com.example.proyectofinal.ui.detallesIncidencias.DetallesIncidenciasSharedViewModel

class IncidenciasActivasFragment : Fragment() {

    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private val sharedViewModel: DetallesIncidenciasSharedViewModel by activityViewModels()
    private val viewModel: IncidenciasActivasViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IncidenciaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_incidencias_activas, container, false)
        recyclerView = view.findViewById(R.id.recyclerIncidenciasActivas)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncidenciaAdapter(emptyList()) { id ->
            Log.d("IncidenciasActivasFragment", "Clic en incidencia con ID: $id")
            sharedViewModel.setIncidenciaId(id)

            findNavController().navigate(
                R.id.action_incidencias_to_detalles,
                Bundle().apply { putLong("id", id.toLong()) }
            )
        }

        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.cargarIncidenciasActivas()
        }

        swipeRefreshLayout.isRefreshing = true
        viewModel.cargarIncidenciasActivas()

        viewModel.incidencias.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
            swipeRefreshLayout.isRefreshing = false
        }

        // Añadir botones y listeners para ordenar
        val fabAula = view.findViewById<View>(R.id.btnAulaActivas)
        val fabFecha = view.findViewById<View>(R.id.btnFechaActivas)
        val fabPrioridad = view.findViewById<View>(R.id.btnPrioridadActivas)

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
