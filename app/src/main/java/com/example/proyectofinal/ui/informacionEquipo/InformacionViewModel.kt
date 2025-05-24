package com.example.proyectofinal.ui.informacionEquipo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Equipo
import kotlinx.coroutines.launch

class InformacionViewModel() : ViewModel() {

    private val _datos = MutableLiveData<List<Equipo>>()
    val datos: LiveData<List<Equipo>> = _datos

    private val apiService = RetrofitClient.apiService

    fun cargarEquipos() {
        viewModelScope.launch {
            try {
                val datos = RetrofitClient.apiService.getEquipos()
                _datos.value = datos
            } catch (e: Exception) {
                e.printStackTrace()
                _datos.value = emptyList()
            }
        }
    }
}
