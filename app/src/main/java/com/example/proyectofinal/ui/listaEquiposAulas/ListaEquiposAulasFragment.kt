package com.example.proyectofinal.ui.listaEquiposAulas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentListaEquiposAulasBinding
import com.example.proyectofinal.model.Aula
import com.example.proyectofinal.model.adapter.AulaAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaEquiposAulasFragment : Fragment() {

    private lateinit var binding: FragmentListaEquiposAulasBinding
    private lateinit var aulaAdapter: AulaAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListaEquiposAulasBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerAulas
        recyclerView.layoutManager = LinearLayoutManager(context)

        aulaAdapter = AulaAdapter(mutableListOf())
        recyclerView.adapter = aulaAdapter

        binding.swipeRefresh.setOnRefreshListener {
            fetchAulas()
        }

        fetchAulas()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchAulas()
    }

    private fun fetchAulas() {
        binding.swipeRefresh.isRefreshing = true
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val aulasResponse = RetrofitClient.apiService.getAulas()
                Log.d("ListaEquiposAulas", "Número de aulas recibidas: ${aulasResponse.size}")

                aulaAdapter.updateAulas(aulasResponse)


                aulaAdapter.setOnItemClickListener { aula ->
                    val bundle = Bundle().apply {
                        aula.id?.let { putLong("id", it) }
                    }
                    findNavController().navigate(
                        R.id.action_listaEquiposAulasFragment_to_equiposFragment,
                        bundle
                    )
                }

            } catch (e: Exception) {
                Log.e("ListaEquiposAulas", "Error al obtener aulas: ${e.message}")
                Toast.makeText(context, "Error al obtener aulas", Toast.LENGTH_SHORT).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
}
