package com.alexmoreno.transportes.modelos

data class Modelo(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precioBase: Double,
    val marcaId : Long,
    val colorId : Long,
    val precioTotal: Double
)