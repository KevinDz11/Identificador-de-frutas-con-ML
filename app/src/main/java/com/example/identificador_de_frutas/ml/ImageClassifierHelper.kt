package com.example.identificador_de_frutas.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.util.Log
import org.tensorflow.lite.support.image.ops.Rot90Op

class ImageClassifierHelper(val context: Context, val classifierListener: ClassifierListener?) {
    private var interpreter: Interpreter? = null

    private val labels = listOf(
        "cabbage", "carrot", "corn", "capsicum", "bell pepper", "apple", "banana",
        "cauliflower", "cucumber", "jalepeno", "ginger", "garlic", "lemon", "eggplant",
        "grapes", "lettuce", "kiwi", "mango", "potato", "peas", "pear", "onion",
        "orange", "pomegranate", "pineapple", "spinach", "sweetpotato", "watermelon",
        "tomato", "beans", "brocoli", "calabacin", "chirimoya", "guanabana", "guayaba",
        "litchi", "maracuya", "okra", "pitahaya", "rambutan", "mandarina", "papaya",
        "coconut", "avocado", "radish", "melon", "pistachio", "nut", "vainilla",
        "betel nut", "alligator apple", "bitter gourd", "bottle gourd", "brazil nut",
        "bread fruit", "cashew", "cocoa bean", "coffee", "common buckthorn",
        "guarana", "leucaena", "lablab"
    )

    init {
        setupInterpreter()
    }

    private fun setupInterpreter() {
        try {
            val model = loadModelFile("modelo_frutas_verduras_Efficient_v3.tflite")
            val options = Interpreter.Options().setNumThreads(2)
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            classifierListener?.onError("Error al inicializar Interpreter: ${e.message}")
        }
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun classify(bitmap: Bitmap, rotation: Int) {
        if (interpreter == null) return

        val imageProcessor = ImageProcessor.Builder()
            // 1. Corregimos la rotación antes de procesar
            .add(Rot90Op(-rotation / 90))
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            // 2. Probamos con normalización estándar (0 a 1)
            .add(NormalizeOp(0f, 255f))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val output = Array(1) { FloatArray(labels.size) }

        interpreter?.run(tensorImage.buffer, output)

        // Convertimos a una lista de pares (Etiqueta, Puntaje) y ordenamos
        val results = labels.mapIndexed { index, label ->
            label to output[0][index]
        }.sortedByDescending { it.second }

        // LOG PARA DEPURACIÓN: Ver los 3 mejores
        val top3 = results.take(3).joinToString { "${it.first}: ${"%.2f".format(it.second)}" }
        Log.d("TFLite", "Top 3: $top3")

        val bestResult = results[0]
        classifierListener?.onResults(bestResult.first, bestResult.second, 0)
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(label: String, score: Float, inferenceTime: Long)
    }
}