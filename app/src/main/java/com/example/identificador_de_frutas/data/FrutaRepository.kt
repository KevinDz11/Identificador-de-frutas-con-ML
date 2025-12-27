package com.example.identificador_de_frutas.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class FrutaRepository(private val context: Context) {

    private var listaFrutas: List<FrutaInfo> = emptyList()

    init {
        listaFrutas = cargarDatosDesdeJSON()
    }

    private fun cargarDatosDesdeJSON(): List<FrutaInfo> {
        val jsonString: String
        try {
            // Lee el archivo desde la carpeta assets
            jsonString = context.assets.open("frutas_datos.json").bufferedReader().use { it.readText() }

            val listType = object : TypeToken<List<FrutaInfo>>() {}.type
            return Gson().fromJson(jsonString, listType)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }
    }

    /**
     * Busca la información de una fruta por su nombre (etiqueta del modelo ML).
     */
    fun obtenerInfoPorNombre(nombreLabel: String): FrutaInfo? {
        return listaFrutas.find { it.nombre.equals(nombreLabel, ignoreCase = true) }
    }
}