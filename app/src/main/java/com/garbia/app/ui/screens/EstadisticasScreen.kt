package com.garbia.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.ui.viewmodel.DiaEstadistica
import com.garbia.app.ui.viewmodel.EstadisticasViewModel
import com.garbia.app.ui.viewmodel.TendenciaMaterial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    navController: NavController,
    viewModel: EstadisticasViewModel = hiltViewModel()
) {
    val datos      by viewModel.datos.collectAsStateWithLifecycle()
    val tendencias by viewModel.tendencias.collectAsStateWithLifecycle()
    val totalEscaneos = datos.sumOf { it.escaneos }
    val totalPuntos   = datos.sumOf { it.puntos }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Últimos 7 días",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCard(
                    valor    = totalEscaneos.toString(),
                    etiqueta = "Escaneos",
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ResumenCard(
                    valor    = totalPuntos.toString(),
                    etiqueta = "Puntos",
                    color    = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Gráfico de barras
            if (datos.isNotEmpty()) {
                Text(
                    "Escaneos por día",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp)
                ) {
                    BarChart(
                        datos    = datos,
                        barColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Puntos por día",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp)
                ) {
                    BarChart(
                        datos    = datos,
                        usePuntos = true,
                        barColor  = MaterialTheme.colorScheme.tertiary,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }

                if (tendencias.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Tendencia por tipo de residuo",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        modifier   = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        LineChart(
                            tendencias = tendencias,
                            etiquetas  = datos.map { it.etiqueta },
                            modifier   = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no hay datos.\n¡Escanea tu primer residuo!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ResumenCard(valor: String, etiqueta: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, fontWeight = FontWeight.Black, fontSize = 28.sp, color = color)
            Text(etiqueta, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BarChart(
    datos: List<DiaEstadistica>,
    usePuntos: Boolean = false,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val valores = datos.map { if (usePuntos) it.puntos.toFloat() else it.escaneos.toFloat() }
    val maxVal  = valores.maxOrNull()?.takeIf { it > 0f } ?: 1f

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val barWidth   = size.width / (datos.size * 2f)
            val spacing    = barWidth
            val chartHeight = size.height

            datos.forEachIndexed { i, _ ->
                val valor     = valores[i]
                val barHeight = (valor / maxVal) * chartHeight
                val x         = i * (barWidth + spacing) + spacing / 2f

                // barra
                drawRoundRect(
                    color       = barColor,
                    topLeft     = Offset(x, chartHeight - barHeight),
                    size        = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // línea base
                drawLine(
                    color       = barColor.copy(alpha = 0.2f),
                    start       = Offset(0f, chartHeight),
                    end         = Offset(size.width, chartHeight),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Etiquetas días
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            datos.forEach { dia ->
                Text(
                    dia.etiqueta,
                    fontSize  = 11.sp,
                    textAlign = TextAlign.Center,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LineChart(
    tendencias: List<TendenciaMaterial>,
    etiquetas: List<String>,
    modifier: Modifier = Modifier
) {
    val maxVal = tendencias.flatMap { it.valoresPorDia }
        .maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val w = size.width
            val h = size.height
            val n = etiquetas.size.coerceAtLeast(2)
            val xStep = w / (n - 1).toFloat()

            // Líneas de guía horizontales
            repeat(5) { i ->
                val y = h * i / 4f
                drawLine(
                    color       = Color.Gray.copy(alpha = 0.15f),
                    start       = Offset(0f, y),
                    end         = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Línea + puntos por tipo de material
            tendencias.forEach { t ->
                val color  = Color(t.colorHex)
                val points = t.valoresPorDia.mapIndexed { i, v ->
                    Offset(i * xStep, h * (1f - v / maxVal))
                }
                for (i in 0 until points.lastIndex) {
                    drawLine(
                        color       = color,
                        start       = points[i],
                        end         = points[i + 1],
                        strokeWidth = 2.5.dp.toPx(),
                        cap         = StrokeCap.Round
                    )
                }
                points.forEach { p ->
                    drawCircle(Color.White, 5.dp.toPx(), p)
                    drawCircle(color, 3.5.dp.toPx(), p)
                }
            }
        }

        // Etiquetas del eje X
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            etiquetas.forEach { label ->
                Text(
                    label,
                    fontSize  = 10.sp,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.weight(1f)
                )
            }
        }

        // Leyenda de colores
        Spacer(Modifier.height(12.dp))
        tendencias.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                fila.forEach { t ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.weight(1f)
                    ) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(Color(t.colorHex), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            t.material,
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
