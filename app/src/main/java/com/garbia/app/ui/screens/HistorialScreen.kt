package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material3.*
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
import com.garbia.app.data.model.Escaneo
import com.garbia.app.ui.viewmodel.HistorialViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TIPOS_FILTRO = listOf("Todos", "Plástico", "Papel", "Vidrio", "Metal", "Orgánico")

private val CONTENEDOR_COLOR = mapOf(
    "Amarillo"  to Color(0xFFFFC107),
    "Azul"      to Color(0xFF2196F3),
    "Verde"     to Color(0xFF4CAF50),
    "Marrón"    to Color(0xFF795548),
    "Gris"      to Color(0xFF9E9E9E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val escaneos by viewModel.escaneos.collectAsStateWithLifecycle()
    val filtro   by viewModel.filtro.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chips de filtro
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TIPOS_FILTRO) { tipo ->
                    val seleccionado = (tipo == "Todos" && filtro == null) ||
                            tipo.equals(filtro, ignoreCase = true)
                    FilterChip(
                        selected = seleccionado,
                        onClick  = {
                            viewModel.setFiltro(if (tipo == "Todos") null else tipo)
                        },
                        label    = { Text(tipo) }
                    )
                }
            }

            if (escaneos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Recycling,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (filtro == null) "Aún no has escaneado nada.\n¡Empieza a reciclar!"
                            else "Sin escaneos de tipo \"$filtro\"",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(escaneos, key = { it.id }) { escaneo ->
                        EscaneoCard(escaneo)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EscaneoCard(escaneo: Escaneo) {
    val color = CONTENEDOR_COLOR[escaneo.contenedor] ?: MaterialTheme.colorScheme.primary
    val fecha = remember(escaneo.fechaTimestamp) {
        SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es", "ES"))
            .format(Date(escaneo.fechaTimestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    contenedorEmoji(escaneo.contenedor),
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(escaneo.tipoMaterial, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "Contenedor ${escaneo.contenedor} · $fecha",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "+${escaneo.puntosGanados} pts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${String.format("%.2f", escaneo.co2Ahorrado)} kg CO₂",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun contenedorEmoji(contenedor: String) = when (contenedor.lowercase()) {
    "amarillo" -> "🟡"
    "azul"     -> "🔵"
    "verde"    -> "🟢"
    "marrón"   -> "🟤"
    else       -> "⚪"
}
