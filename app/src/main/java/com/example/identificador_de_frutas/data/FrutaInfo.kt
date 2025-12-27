package com.example.identificador_de_frutas.data

data class FrutaInfo(
    val nombre: String,
    val almacenamiento: String,
    val tipAlmacenamiento: String,
    val tiempoVida: String,
    val esTemporada: Boolean,
    val indiceGlucemico: String,
    val superPoder: String,
    val precioReferencia: Double
)