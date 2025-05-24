package com.example.proyectofinal.ui.consultarIncidencias

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectofinal.ui.consultarIncidencias.activas.IncidenciasActivasFragment
import com.example.proyectofinal.ui.consultarIncidencias.creadas.IncidenciasCreadasFragment
import com.example.proyectofinal.ui.consultarIncidencias.resueltas.IncidenciasResueltasFragment

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IncidenciasCreadasFragment()
            1 -> IncidenciasActivasFragment()
            2 -> IncidenciasResueltasFragment()
            else -> throw IllegalArgumentException("Posición inválida")
        }
    }
}
