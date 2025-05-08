package com.alexmoreno.transportes.modelos

data class Marca(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val anioFundacion: Int,
    val modelos: List<Modelo> = emptyList()
)