package com.example.proyectofinal.model

import java.util.*

data class Incidencia(
    val id: Long? = null,
    val clase: String,
    val idaula: Int,
    val estado: String,
    val autor: String,
    val fecha: Date,
    val descripcion: String,
    val solucion: String,
    val prioridad: String,
    val idequipo: Int,
    val nombreaula: String?,
    val nombreequipo: String,

    )
