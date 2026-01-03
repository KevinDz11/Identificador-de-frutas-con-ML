package com.example.identificador_de_frutas.ui.scanner

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.identificador_de_frutas.data.FrutaInfo // Import crucial
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(viewModel: ScannerViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Escáner de Frutas") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                                        val rotation = imageProxy.imageInfo.rotationDegrees // Obtenemos la rotación (0, 90, 180, 270)
                                        val bitmap = imageProxy.toBitmap()
                                        if (bitmap != null) {
                                            // Pasamos el bitmap Y la rotación
                                            viewModel.onFrameCaptured(bitmap, rotation)
                                        }
                                        imageProxy.close()
                                    }
                                }

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalyzer
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )

            // UI de información
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (val state = uiState) {
                        is ScannerUiState.Success -> {
                            Text("Detectado: ${state.nombre}", style = MaterialTheme.typography.headlineSmall)
                            state.datosExtra?.let { info ->
                                Spacer(Modifier.height(8.dp))
                                Text("📍 Almacenar en: ${info.almacenamiento}")
                                Text("💡 Tip: ${info.tipAlmacenamiento}")
                                Text("🍎 Superpoder: ${info.superPoder}")
                                Text("📊 IG: ${info.indiceGlucemico}", color = MaterialTheme.colorScheme.primary)
                                Text("💰 Precio aprox: $${info.precioReferencia}/kg")
                            }
                        }
                        is ScannerUiState.Empty -> Text("Apunta a una fruta para analizar")
                        is ScannerUiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

//fun ImageProxy.toBitmap(): Bitmap? {
//    val buffer = planes[0].buffer
//    buffer.rewind()
//    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    bitmap.copyPixelsFromBuffer(buffer)
//    return bitmap
//}