package com.example.proyectofinal.ui.registrarEquipo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.api.RetrofitClient
import com.example.proyectofinal.model.Equipo
import kotlinx.coroutines.launch
import retrofit2.Response // Asegúrate de importar esto

class RegistrarViewModel : ViewModel() {

    private val _datos = MutableLiveData<List<Equipo>>()
    val datos: LiveData<List<Equipo>> = _datos

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService = RetrofitClient.apiService

    fun insertarEquipos(
        marca: String,
        modelo: String,
        procesador: String,
        ram: Int,
        cpu: String,
        ndiscos: Int,
        tipodiscos: String,
        grafica: String,
        bluetooth: Boolean,
        wifi: Boolean,
        codigo: String,
        aula: String,
        so: String,
        imageBase64: String
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val miEquipo = Equipo(
                    id = 0L,
                    marca = marca,
                    modelo = modelo,
                    procesador = procesador,
                    ram = ram,
                    cpu = cpu,
                    ndiscos = ndiscos,
                    tipodiscos = tipodiscos,
                    grafica = grafica,
                    bluetooth = bluetooth,
                    wifi = wifi,
                    codigo = codigo,
                    aula = aula,
                    so = so,
                    foto = imageBase64
                )


                val response: Response<Equipo> = apiService.crearEquipo(miEquipo)

                if (response.isSuccessful) {
                    val equipoCreado = response.body()
                    equipoCreado?.let {
                        _datos.value = listOf(it)
                    }


                } else {
                    _errorMessage.value = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepción: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}