package com.alexmoreno.transportes.models

data class Marca(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val anioFundacion: Int,
    val modelos: List<Modelo> = emptyList()
)