package com.example.identificador_de_frutas.ui.scanner

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.example.identificador_de_frutas.data.FrutaInfo
import com.example.identificador_de_frutas.data.FrutaRepository
import com.example.identificador_de_frutas.ml.ImageClassifierHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel encargado de gestionar el estado del escáner y la información de la fruta detectada.
 */
class ScannerViewModel(application: Application) : AndroidViewModel(application), ImageClassifierHelper.ClassifierListener {

    private val repository = FrutaRepository(application)

    // Declaramos los estados de la UI PRIMERO.
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Empty)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    // El helper se inicializa después de que los estados están listos
    private val classifierHelper = ImageClassifierHelper(application, this)

    /**
     * Función que recibe un frame de la cámara (en formato Bitmap) y lo procesa.
     */
    fun onFrameCaptured(bitmap: Bitmap, rotation: Int) {
        classifierHelper.classify(bitmap, rotation)
    }

    // --- Implementación de ClassifierListener ---

    override fun onError(error: String) {
        _uiState.value = ScannerUiState.Error(error)
    }

    // CORRECCIÓN: La firma debe coincidir exactamente con la del ImageClassifierHelper corregido
    override fun onResults(label: String, score: Float, inferenceTime: Long) {
        // Solo procesamos si la confianza es mayor al 50%
        if (score > 0.1f) {
            val infoExtra = repository.obtenerInfoPorNombre(label)

            _uiState.value = ScannerUiState.Success(
                nombre = label,
                confianza = score,
                datosExtra = infoExtra
            )
        }
    }
}

/**
 * Representa los diferentes estados de la pantalla de escaneo.
 */
sealed class ScannerUiState {
    object Empty : ScannerUiState()

    data class Success(
        val nombre: String,
        val confianza: Float,
        val datosExtra: FrutaInfo?
    ) : ScannerUiState()

    data class Error(val message: String) : ScannerUiState()
}