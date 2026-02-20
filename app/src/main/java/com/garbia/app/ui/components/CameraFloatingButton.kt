package com.garbia.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbia.app.ui.Screen

@Composable
fun CameraFloatingButton(
    navController: NavController,
    modifier: Modifier = Modifier // <-- NUEVO: Recibimos las órdenes de diseño desde fuera
) {
    val mainColor = MaterialTheme.colorScheme.primary

    FloatingActionButton(
        onClick = { navController.navigate(Screen.Camera.route) },
        shape = CircleShape,
        containerColor = mainColor,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp),
        modifier = modifier // <-- Aplicamos las órdenes aquí
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Escanear Residuo",
            modifier = Modifier.size(32.dp)
        )
    }
}