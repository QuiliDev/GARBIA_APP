package com.garbia.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                username      = uiState.nombre,
                level         = uiState.nivelLabel,
                currentXp     = uiState.puntosEnNivel,
                targetXp      = uiState.puntosParaSiguienteNivel,
                navController = navController
            )

            DashboardStats(
                escaneos = uiState.escaneosTotales,
                puntos   = uiState.puntosTotales,
                co2      = uiState.co2Ahorrado,
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
            )

            if (uiState.rachaActual > 0) {
                RachaWidget(
                    rachaActual = uiState.rachaActual,
                    rachaMáxima = uiState.rachaMáxima,
                    modifier    = Modifier
                        .offset(y = (-30).dp)
                        .padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(4.dp).offset(y = (-30).dp))
            }

            GuiaQuickAccessCard(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .padding(horizontal = 24.dp),
                onClick  = { navController.navigate(com.garbia.app.ui.Screen.Guia.route) }
            )

            Spacer(modifier = Modifier.height(10.dp).offset(y = (-40).dp))

            DidYouKnowSection(modifier = Modifier.offset(y = (-40).dp))

            RecentActivitySection(
                historial = uiState.historialReciente,
                modifier  = Modifier.offset(y = (-40).dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        AnimatedVisibility(
            visible = showTopBar,
            enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit    = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            GarbiaTopBar(navController, isOverDarkBackground = true)
        }
    }
}

@Composable
private fun GuiaQuickAccessCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick   = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("♻️", fontSize = 26.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Guía de Reciclaje",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "¿Dónde va cada residuo?",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun RachaWidget(rachaActual: Int, rachaMáxima: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Racha: $rachaActual ${if (rachaActual == 1) "día" else "días"} consecutivos",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color(0xFFE65100)
                )
                Text(
                    "Récord personal: $rachaMáxima días · ¡Sigue así!",
                    fontSize = 12.sp,
                    color    = Color(0xFFBF360C)
                )
            }
        }
    }
}
