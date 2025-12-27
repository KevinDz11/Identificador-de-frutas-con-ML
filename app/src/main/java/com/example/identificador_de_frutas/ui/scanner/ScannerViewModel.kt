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
import org.tensorflow.lite.task.vision.classifier.Classifications

/**
 * ViewModel encargado de gestionar el estado del escáner y la información de la fruta detectada.
 */
class ScannerViewModel(application: Application) : AndroidViewModel(application), ImageClassifierHelper.ClassifierListener {

    private val repository = FrutaRepository(application)
    private val classifierHelper = ImageClassifierHelper(application, this)

    // Estado para la UI: La fruta detectada actualmente con su información extra
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Empty)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    /**
     * Función que recibe un frame de la cámara (en formato Bitmap) y lo procesa.
     */
    fun onFrameCaptured(bitmap: Bitmap) {
        classifierHelper.classify(bitmap)
    }

    // --- Implementación de ClassifierListener ---

    override fun onError(error: String) {
        _uiState.value = ScannerUiState.Error(error)
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        val result = results?.firstOrNull()?.categories?.firstOrNull()

        if (result != null && result.score > 0.5f) {
            val nombreDetectado = result.label
            val infoExtra = repository.obtenerInfoPorNombre(nombreDetectado)

            _uiState.value = ScannerUiState.Success(
                nombre = nombreDetectado,
                confianza = result.score,
                datosExtra = infoExtra
            )
        } else {
            // Si no detecta nada claro, podemos mantener el estado previo o limpiar
            // _uiState.value = ScannerUiState.Empty
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