package com.example.proyectofinal.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.api.RetrofitClient
import kotlinx.coroutines.launch

class EquipoViewModel : ViewModel() {
    private val _equipos = mutableStateListOf<Equipo>()
    val equipos: List<Equipo> get() = _equipos

    fun cargarEquipos() {

    }


}