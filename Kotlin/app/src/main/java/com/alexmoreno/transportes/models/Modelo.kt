package com.alexmoreno.transportes.models

data class Modelo(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precioBase: Double,
    val marcaId : Long
)