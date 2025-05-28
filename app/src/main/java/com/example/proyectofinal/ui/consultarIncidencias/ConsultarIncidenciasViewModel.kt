package com.example.proyectofinal.ui.consultarIncidencias

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Incidencia
import kotlinx.coroutines.launch

class ConsultarIncidenciasViewModel : ViewModel() {

    private val _incidencias = MutableLiveData<List<Incidencia>>()
    val incidencias: LiveData<List<Incidencia>> get() = _incidencias

    fun cargarIncidenciasFiltradas(estado: String, orden: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getIncidenciasFiltradas(estado, orden)
                if (response.isSuccessful) {
                    _incidencias.value = response.body() ?: emptyList()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error cargando incidencias", e)
            }
        }
    }
}
