package com.example.proyectofinal.ui.consultarIncidencias.resueltas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Incidencia
import kotlinx.coroutines.launch


class IncidenciasResueltasViewModel : ViewModel() {

    private val _incidenciasResueltas = MutableLiveData<List<Incidencia>>()
    val incidenciasResueltas: LiveData<List<Incidencia>> get() = _incidenciasResueltas

    private var ordenActual = "aula"  // Orden por defecto

    fun setOrden(orden: String) {
        ordenActual = orden
        cargarIncidenciasResueltas()
    }

    fun cargarIncidenciasResueltas() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getIncidenciasOrdenadas(
                    listOf("resuelta"),
                    ordenActual
                )
                if (response.isSuccessful && response.body() != null) {
                    _incidenciasResueltas.value = response.body()
                } else {
                    Log.e("API", "Respuesta vacía o error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error cargando incidencias resueltas", e)
            }
        }
    }
}
