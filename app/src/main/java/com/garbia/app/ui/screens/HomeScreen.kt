package com.garbia.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbia.app.ui.components.*
import com.garbia.app.ui.components.GarbiaTopBar

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val background = MaterialTheme.colorScheme.background

    // Lógica para mostrar/ocultar la TopBar al hacer scroll
    val showTopBar by remember {
        derivedStateOf { scrollState.value < 100 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        // 1. EL CONTENIDO (Con Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Cabecera grande (Ahora parametrizable si quieres)
            HomeHeader(username = "Anthony", level = "Nivel 5: Experto")

            // Dashboard (-40dp para solapar sobre el header)
            DashboardStats(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
            )

            // Espacio de ajuste
            Spacer(modifier = Modifier.height(10.dp).offset(y = (-40).dp))

            // Sección de Tips
            DidYouKnowSection(modifier = Modifier.offset(y = (-40).dp))

            // Sección de Actividad
            RecentActivitySection(modifier = Modifier.offset(y = (-40).dp))

            // Espacio final para no chocar con la BottomBar
            Spacer(modifier = Modifier.height(100.dp))
        }

        // 2. LA TOPBAR FLOTANTE (Animada)
        AnimatedVisibility(
            visible = showTopBar,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            GarbiaTopBar(navController, isOverDarkBackground = true)
        }
    }
}