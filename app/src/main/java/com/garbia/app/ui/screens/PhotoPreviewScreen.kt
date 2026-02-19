package com.garbia.app.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme // <-- NUEVO IMPORT
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun PhotoPreviewScreen(navController: NavController, photoUri: String) {
    val mainColor = MaterialTheme.colorScheme.primary

    // Convertimos el texto (String) de vuelta a un enlace (Uri)
    val uri = Uri.parse(photoUri)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // 1. LA FOTO CAPTURADA EN PANTALLA COMPLETA
        AsyncImage(
            model = uri,
            contentDescription = "Foto capturada",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Recorta la imagen para que llene la pantalla
        )

        // 2. BOTONERA INFERIOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // BOTÓN REPETIR (Se queda en gris oscuro para no quitar protagonismo)
            Button(
                onClick = { navController.popBackStack() }, // Nos devuelve a la cámara
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Repetir", color = Color.White, fontSize = 16.sp)
            }

            // BOTÓN ANALIZAR (Ahora es dinámico)
            Button(
                onClick = {
                    // TODO: ¡Aquí conectaremos con la Inteligencia Artificial!
                },
                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
            ) {
                Text("Analizar Residuo", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}