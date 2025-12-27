package com.example.identificador_de_frutas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.identificador_de_frutas.data.FrutaInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    fruta: FrutaInfo,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = fruta.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Sección 1: Gestión de Alacena
            InfoSection(
                titulo = "🏠 Almacenamiento",
                contenido = "Guardar en: ${fruta.almacenamiento}\n" +
                        "Tip: ${fruta.tipAlmacenamiento}\n" +
                        "Duración estimada: ${fruta.tiempoVida}"
            )

            // Sección 2: Salud
            val temporadaTxt = if (fruta.esTemporada) "¡Está de temporada! 🌟" else "No es temporada"
            InfoSection(
                titulo = "🍎 Salud y Nutrición",
                contenido = "Superpoder: ${fruta.superPoder}\n" +
                        "Índice Glucémico: ${fruta.indiceGlucemico}\n" +
                        "Estatus: $temporadaTxt"
            )

            // Sección 3: Economía
            InfoSection(
                titulo = "💰 Mercado",
                contenido = "Precio promedio sugerido: $${fruta.precioReferencia} /kg"
            )
        }
    }
}

@Composable
fun InfoSection(titulo: String, contenido: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = contenido,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}