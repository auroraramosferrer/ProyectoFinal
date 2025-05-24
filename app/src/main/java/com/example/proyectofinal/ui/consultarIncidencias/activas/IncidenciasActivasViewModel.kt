package com.example.proyectofinal.ui.consultarIncidencias.activas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Incidencia
import kotlinx.coroutines.launch

class IncidenciasActivasViewModel : ViewModel() {

    private val _incidencias = MutableLiveData<List<Incidencia>>()
    val incidencias: LiveData<List<Incidencia>> get() = _incidencias

    private var ordenActual = "aula"  // Por defecto, igual que en creadas

    fun setOrden(orden: String) {
        ordenActual = orden
        cargarIncidenciasActivas()
    }

    fun cargarIncidenciasActivas() {
        viewModelScope.launch {
            try {
                // Aquí llamamos a la API con ordenActual
                val response = RetrofitClient.apiService.getIncidenciasOrdenadas(
                    listOf("procesada"),
                    ordenActual
                )
                if (response.isSuccessful) {
                    Log.d("API", "Datos recibidos: ${response.body()}")
                    _incidencias.value = response.body() ?: emptyList()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error cargando incidencias activas", e)
            }
        }
    }
}
