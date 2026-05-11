package com.garbia.app.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.data.model.Logro
import com.garbia.app.ui.viewmodel.LogrosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    navController: NavController,
    viewModel: LogrosViewModel = hiltViewModel()
) {
    val logros        by viewModel.logros.collectAsStateWithLifecycle()
    val desbloqueados = logros.count { it.desbloqueado }
    val context       = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Logros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val logrosNombres = logros.filter { it.desbloqueado }.joinToString(" · ") { "${it.emoji} ${it.nombre}" }
                        val texto = "¡Llevo $desbloqueados/${logros.size} logros en GarbiaApp! 🏆\n$logrosNombres\n#Garbia #Reciclaje"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, texto)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir logros"))
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Progreso general
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "$desbloqueados / ${logros.size} logros desbloqueados",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (logros.isEmpty()) 0f else desbloqueados.toFloat() / logros.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(logros, key = { it.id }) { logro ->
                    LogroCard(logro)
                }
            }
        }
    }
}

@Composable
private fun LogroCard(logro: Logro) {
    val scale by animateFloatAsState(
        targetValue = if (logro.desbloqueado) 1f else 0.95f,
        label = "logro_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.desbloqueado)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(if (logro.desbloqueado) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (logro.desbloqueado)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            CircleShape
                        )
                )
                Text(
                    logro.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.alpha(if (logro.desbloqueado) 1f else 0.3f)
                )
                if (!logro.desbloqueado) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                logro.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (logro.desbloqueado)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                logro.descripcion,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (logro.desbloqueado) 0.8f else 0.4f
                ),
                lineHeight = 15.sp
            )
        }
    }
}
