package com.example.identificador_de_frutas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.identificador_de_frutas.ui.scanner.CameraScreen
import com.example.identificador_de_frutas.ui.theme.Identificador_de_frutasTheme

class MainActivity : ComponentActivity() {

    // Registramos el lanzador para solicitar el permiso de la cámara
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si se concede, refrescamos el contenido para mostrar la cámara
            setAppContent()
        } else {
            // Si se deniega, avisamos al usuario
            Toast.makeText(
                this,
                "Se necesita el permiso de cámara para escanear frutas",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificamos el permiso al iniciar
        checkCameraPermission()

        // Establecemos el contenido inicial
        setAppContent()
    }

    private fun setAppContent() {
        setContent {
            Identificador_de_frutasTheme {
                // Llamamos a tu CameraScreen en lugar del Greeting por defecto
                CameraScreen()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tenemos el permiso, lo solicitamos
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}