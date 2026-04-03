package com.garbia.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- MODELOS ---
data class RecyclingPoint(
    val name: String,
    val address: String,
    val distance: String,
    val types: List<String>,
    val color: Color,
    val isOpen: Boolean,
    val hours: String,
    val icon: ImageVector,
    // posición normalizada [0,1] en el canvas del mapa
    val mapX: Float,
    val mapY: Float
)

// --- DATOS ESTÁTICOS ---
private val recyclingPoints = listOf(
    RecyclingPoint(
        name = "Punto Verde Central",
        address = "Plaza Mayor, 12",
        distance = "350 m",
        types = listOf("Vidrio", "Plástico", "Papel"),
        color = Color(0xFF4CAF50),
        isOpen = true,
        hours = "24 h",
        icon = Icons.Outlined.Recycling,
        mapX = 0.30f, mapY = 0.28f
    ),
    RecyclingPoint(
        name = "EcoPoint Supermercado",
        address = "Av. de la Constitución, 45",
        distance = "1.2 km",
        types = listOf("Plástico", "Papel", "Metal"),
        color = Color(0xFF2196F3),
        isOpen = true,
        hours = "09:00 – 21:00",
        icon = Icons.Outlined.Store,
        mapX = 0.70f, mapY = 0.35f
    ),
    RecyclingPoint(
        name = "Contenedor Especial",
        address = "C/ Hospital, 3",
        distance = "2.1 km",
        types = listOf("Pilas", "Electrónica"),
        color = Color(0xFFFF9800),
        isOpen = false,
        hours = "08:00 – 15:00",
        icon = Icons.Outlined.BatteryAlert,
        mapX = 0.20f, mapY = 0.68f
    ),
    RecyclingPoint(
        name = "Punto Ayuntamiento",
        address = "C/ del Ayuntamiento, 1",
        distance = "800 m",
        types = listOf("General", "Orgánico"),
        color = Color(0xFF9C27B0),
        isOpen = true,
        hours = "07:00 – 22:00",
        icon = Icons.Outlined.AccountBalance,
        mapX = 0.75f, mapY = 0.65f
    ),
    RecyclingPoint(
        name = "EcoParque Municipal",
        address = "Camino del Parque, s/n",
        distance = "3.4 km",
        types = listOf("Todos los tipos"),
        color = Color(0xFFE91E63),
        isOpen = true,
        hours = "08:00 – 20:00",
        icon = Icons.Outlined.Park,
        mapX = 0.45f, mapY = 0.82f
    ),
)

private val ocean     = Color(0xFF0277BD)
private val oceanDark = Color(0xFF01579B)

// --- PANTALLA ---
@Composable
fun MapasScreen(navController: NavController) {
    val primary = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── CABECERA ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Brush.linearGradient(listOf(ocean, oceanDark)),
                            RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                ) {
                    Box(Modifier.offset(160.dp, (-40).dp).size(200.dp).background(Color.White.copy(.07f), CircleShape))
                    Box(Modifier.offset((-30).dp, 70.dp).size(130.dp).background(Color.White.copy(.05f), CircleShape))

                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Outlined.ArrowBackIosNew, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Icon(Icons.Outlined.Map, null, tint = Color.White, modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Puntos de Reciclaje", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.MyLocation, null, tint = Color(0xFF80DEEA), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Valencia, España · ${recyclingPoints.size} puntos cercanos",
                                color = Color.White.copy(.8f), fontSize = 13.sp)
                        }
                    }
                }
            }

            // ── MAPA ESTÁTICO ─────────────────────────────────────────
            item {
                MapCanvas(points = recyclingPoints, userColor = primary)
            }

            // ── LEYENDA ───────────────────────────────────────────────
            item { MapLegend() }

            // ── TÍTULO LISTA ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Puntos cercanos a ti", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                    Surface(color = MaterialTheme.colorScheme.primary.copy(.1f), shape = RoundedCornerShape(8.dp)) {
                        Text("${recyclingPoints.count { it.isOpen }} abiertos",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            // ── TARJETAS PUNTOS ───────────────────────────────────────
            items(recyclingPoints.sortedBy { it.distance.replace(" km","").replace(" m","").toFloat()
                .let { v -> if (it.distance.endsWith("km")) v * 1000 else v } }) { point ->
                RecyclingPointCard(point)
                Spacer(Modifier.height(10.dp))
            }

            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}

// --- CANVAS DEL MAPA ---
@Composable
private fun MapCanvas(points: List<RecyclingPoint>, userColor: Color) {
    val mapBg    = Color(0xFFECEFF1)
    val block    = Color(0xFFD0D8E0)
    val road     = Color(0xFFFFFFFF)
    val park     = Color(0xFFA5D6A7)
    val water    = Color(0xFFB3E5FC)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                val w = size.width
                val h = size.height

                // Fondo del mapa
                drawRect(mapBg)

                // Bloques de manzanas
                val cols = 5
                val rows = 4
                val vStep = w / cols
                val hStep = h / rows
                val road  = 14f

                for (row in 0 until rows) {
                    for (col in 0 until cols) {
                        val isPark  = (row == 1 && col == 2)
                        val isWater = (row == 0 && col == 4)
                        val color   = if (isPark) park else if (isWater) water else block
                        drawRect(
                            color,
                            topLeft = Offset(col * vStep + road, row * hStep + road),
                            size = Size(vStep - road * 2, hStep - road * 2)
                        )
                    }
                }

                // Líneas de carreteras (horizontales)
                for (i in 0..rows) {
                    drawRect(Color.White, topLeft = Offset(0f, i * hStep - road / 2), size = Size(w, road))
                }
                // Líneas de carreteras (verticales)
                for (i in 0..cols) {
                    drawRect(Color.White, topLeft = Offset(i * vStep - road / 2, 0f), size = Size(road, h))
                }

                // Líneas de marcado de carril (punteado)
                val dash = 20f
                val gap  = 12f
                for (i in 0..rows) {
                    var x = 0f
                    while (x < w) {
                        drawLine(Color(0xFFBDBDBD), Offset(x, i * hStep), Offset((x + dash).coerceAtMost(w), i * hStep), strokeWidth = 1.5f)
                        x += dash + gap
                    }
                }

                // Marcadores de puntos de reciclaje
                points.forEach { point ->
                    val cx = point.mapX * w
                    val cy = point.mapY * h
                    // Sombra
                    drawCircle(Color.Black.copy(.15f), radius = 18f, center = Offset(cx + 2f, cy + 2f))
                    // Círculo exterior
                    drawCircle(point.color, radius = 17f, center = Offset(cx, cy))
                    // Círculo interior blanco
                    drawCircle(Color.White, radius = 8f, center = Offset(cx, cy))
                    // Punto de color interior
                    drawCircle(point.color, radius = 4f, center = Offset(cx, cy))
                }

                // Posición del usuario (más grande y con anillo)
                val ux = 0.48f * w
                val uy = 0.47f * h
                drawCircle(userColor.copy(.25f), radius = 26f, center = Offset(ux, uy))
                drawCircle(userColor, radius = 14f, center = Offset(ux, uy))
                drawCircle(Color.White, radius = 6f, center = Offset(ux, uy))
            }
        }
    }
}

// --- LEYENDA ---
@Composable
private fun MapLegend() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(MaterialTheme.colorScheme.primary, "Tu posición")
            LegendItem(Color(0xFF4CAF50), "Punto Verde")
            LegendItem(Color(0xFF2196F3), "Eco-Shop")
            LegendItem(Color(0xFFFF9800), "Especiales")
            LegendItem(Color(0xFF9C27B0), "Municipal")
            LegendItem(Color(0xFFE91E63), "EcoParque")
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.height(3.dp))
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(.6f), fontWeight = FontWeight.Medium)
    }
}

// --- TARJETA PUNTO DE RECICLAJE ---
@Composable
private fun RecyclingPointCard(point: RecyclingPoint) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icono del punto
            Box(
                Modifier.size(54.dp).background(point.color.copy(.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(point.icon, null, tint = point.color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(point.name, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    // Estado
                    Surface(
                        color = if (point.isOpen) Color(0xFF4CAF50).copy(.1f) else Color(0xFFE53935).copy(.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            if (point.isOpen) "Abierto" else "Cerrado",
                            fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = if (point.isOpen) Color(0xFF4CAF50) else Color(0xFFE53935),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(point.address, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(point.hours, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                // Tipos aceptados
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    point.types.forEach { type ->
                        Surface(
                            color = point.color.copy(.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(type, fontSize = 10.sp, color = point.color, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            // Distancia
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.NearMe, null, tint = point.color, modifier = Modifier.size(20.dp))
                Text(point.distance, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = point.color)
            }
        }
    }
}
