
package com.example.proyectofinal.ui.detasllesIncidencias

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetallesIncidenciasSharedViewModel : ViewModel() {
    private val _incidenciaSeleccionada = MutableLiveData<Int?>()
    private val _incidenciaId = MutableLiveData<Int>()
    val incidenciaSeleccionada: LiveData<Int?> = _incidenciaSeleccionada
    val incidenciaId: LiveData<Int> get() = _incidenciaId

    fun seleccionarIncidencia(id: Int) {
        _incidenciaSeleccionada.value = id
    }

    fun limpiarSeleccion() {
        _incidenciaSeleccionada.value = null
    }

    fun setIncidenciaId(id: Int) {
        _incidenciaId.value = id
    }
}

