package com.example.proyectofinal.ui.consultarIncidencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectofinal.R
import com.example.proyectofinal.ui.detasllesIncidencias.DetallesIncidenciasSharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ConsultarIncidenciasFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNav: BottomNavigationView
    private val orderByIncidenciasViewModel: OrderByIncidenciasViewModel by activityViewModels()
    private val viewModel: ConsultarIncidenciasViewModel by viewModels()
    private val sharedViewModel: DetallesIncidenciasSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consultar_incidencias, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        bottomNav = view.findViewById(R.id.bottomNav)

        viewPager.adapter = TabsAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Creadas"
                1 -> "En proceso"
                2 -> "Resueltas"
                else -> "Tab ${position + 1}"
            }
        }.attach()

        bottomNav.setOnItemSelectedListener { item ->
            val orden = when (item.itemId) {
                R.id.menu_option_1 -> "fecha"
                R.id.menu_option_2 -> "prioridad"
                R.id.menu_option_3 -> "aula"
                else -> "fecha"
            }
            val estado = when (viewPager.currentItem) {
                0 -> "creada"
                1 -> "procesada"
                2 -> "resuelta"
                else -> "creada"
            }
            viewModel.cargarIncidenciasFiltradas(estado, orden)
            true
        }

        sharedViewModel.incidenciaSeleccionada.observe(viewLifecycleOwner) { id ->
            id?.let {
                val bundle = Bundle().apply { putInt("id", it) }
                findNavController().navigate(R.id.action_incidencias_to_detalles, bundle)
                sharedViewModel.limpiarSeleccion()
            }
        }

        return view
    }
}
