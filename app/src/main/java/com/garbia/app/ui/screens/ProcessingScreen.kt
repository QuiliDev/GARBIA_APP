package com.garbia.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.ui.viewmodel.ProcessingViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProcessingScreen(
    navController: NavController,
    photoUri: String,
    viewModel: ProcessingViewModel = hiltViewModel()
) {
    val mainColor  = MaterialTheme.colorScheme.primary
    val statusText by viewModel.statusText.collectAsStateWithLifecycle()
    val analisisDone by viewModel.analisisDone.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.analizarFoto(photoUri)
    }

    LaunchedEffect(analisisDone) {
        if (analisisDone) {
            val encodedUri = URLEncoder.encode(photoUri, StandardCharsets.UTF_8.toString())
            navController.navigate("result_screen/$encodedUri") {
                popUpTo("camera_screen") { inclusive = false }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(120.dp), color = mainColor, strokeWidth = 8.dp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = statusText, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Gray, textAlign = TextAlign.Center)
    }
}
