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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.ui.components.*
import com.garbia.app.ui.components.GarbiaTopBar
import com.garbia.app.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val background = MaterialTheme.colorScheme.background

    val showTopBar by remember {
        derivedStateOf { scrollState.value < 100 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            HomeHeader(
                username = uiState.nombre,
                level = uiState.nivelLabel,
                currentXp = uiState.puntosEnNivel,
                targetXp = uiState.puntosParaSiguienteNivel,
                navController = navController
            )

            DashboardStats(
                escaneos = uiState.escaneosTotales,
                puntos = uiState.puntosTotales,
                co2 = uiState.co2Ahorrado,
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(10.dp).offset(y = (-40).dp))

            DidYouKnowSection(modifier = Modifier.offset(y = (-40).dp))

            RecentActivitySection(
                historial = uiState.historialReciente,
                modifier = Modifier.offset(y = (-40).dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

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
