package com.garbia.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProcessingScreen(navController: NavController, photoUri: String) {
    val mainColor = MaterialTheme.colorScheme.primary
    var statusText by remember { mutableStateOf("Subiendo imagen...") }

    LaunchedEffect(Unit) {
        // 1. Simulación de pasos
        delay(2000)
        statusText = "Consultando modelo IA..."
        delay(2000)
        statusText = "Identificando material..."
        delay(1500)

        // CORRECCIÓN: Volvemos a codificar la URI antes de enviarla
        // Como 'photoUri' aquí ya tiene las barras normales (file:///...),
        // hay que protegerla de nuevo para que viaje segura.
        val encodedUri = URLEncoder.encode(photoUri, StandardCharsets.UTF_8.toString())

        // 3. Navegamos usando la versión codificada
        navController.navigate("result_screen/$encodedUri") {
            // Esto borra el historial para que al volver atrás no vuelvas a "Cargando"
            popUpTo("camera_screen") { inclusive = false }
        }
    }

    // --- DISEÑO VISUAL ---
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(120.dp),
            color = mainColor,
            strokeWidth = 8.dp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = statusText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}