package com.example.proyectofinal.ui.crearIncidencias


import androidx.lifecycle.ViewModel
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Incidencia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrearIncidenciasViewModel : ViewModel() {

    fun insertarIncidencia(incidencia: Incidencia, callback: (Incidencia?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val creada = RetrofitClient.apiService.crearIncidencia(incidencia)
                callback(creada)  // Ya es la incidencia creada
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

}
