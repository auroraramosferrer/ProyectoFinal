package com.example.proyectofinal.model

data class Equipo (
    val id: Long? = null,
    val marca: String,
    val modelo: String,
    val procesador : String,
    val ram : Int,
    val cpu : String,
    val ndiscos : Int,
    val tipodiscos : String,
    val grafica : String,
    val bluetooth : Boolean,
    val wifi : Boolean,
    val codigo : String,
    val aula : String,
    val so : String,
    val foto : String,

    )

