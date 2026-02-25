package com.garbia.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 1. CABECERA DE INICIO ---
@Composable
fun HomeHeader(
    username: String = "Anthony",
    level: String = "Nivel 5: Experto",
    currentXp: Int = 1200,
    targetXp: Int = 1500
) {
    val mainColor = MaterialTheme.colorScheme.primary
    val infiniteTransition = rememberInfiniteTransition(label = "background_anim")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse), label = "c1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -25f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse), label = "c2"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(mainColor, Color(0xFF00796B)),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
    ) {
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))) {
            Box(modifier = Modifier.offset(x = 120.dp + offset1.dp, y = (-60).dp + offset1.dp).size(250.dp).background(Color.White.copy(alpha = 0.08f), CircleShape))
            Box(modifier = Modifier.offset(x = (-40).dp + offset2.dp, y = 80.dp).size(160.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .padding(top = 80.dp)
        ) {
            Text(text = "Hola, $username 👋", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
            Text(text = "¡Vamos a cambiar el mundo hoy!", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = "🏆", fontSize = 14.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(text = level, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "$currentXp", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(text = " / $targetXp XP", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
            }

            // Barra de progreso calculada
            val progress = (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f)
            Box(modifier = Modifier.fillMaxWidth().height(10.dp)) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)).background(Color.Black.copy(alpha = 0.2f)))
                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFC107)))
            }

            Text(text = "¡Estás a nada de subir de nivel!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickActionItem(Icons.Outlined.Leaderboard, "Ranking")
                QuickActionItem(Icons.Outlined.CardGiftcard, "Premios")
                QuickActionItem(Icons.Outlined.Lightbulb, "Tips")
                QuickActionItem(Icons.Outlined.Map, "Mapa")
            }
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(45.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)).clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// --- 2. DASHBOARD (ESTADÍSTICAS) ---
@Composable
fun DashboardStats(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(10.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.0f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 22.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.QrCodeScanner, 50, "", "Escaneos")
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.Gray.copy(alpha = 0.2f)))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.MonetizationOn, 1250, "", "Puntos", true)
            }
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.Gray.copy(alpha = 0.2f)))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.Co2, 5, "kg", "Ahorrados")
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, targetValue: Int, suffix: String, label: String, isPrimary: Boolean = false) {
    val color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val counter = remember { Animatable(0f) }
    LaunchedEffect(key1 = targetValue) {
        counter.animateTo(targetValue.toFloat(), animationSpec = tween(1500, easing = FastOutSlowInEasing))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${counter.value.toInt()}$suffix", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

// --- 3. SECCIÓN "SABÍAS QUE" (TIPS) ---
data class TipData(val title: String, val icon: ImageVector, val color: Color, val description: String)

@Composable
fun DidYouKnowSection(modifier: Modifier = Modifier) {
    val tips = listOf(
        TipData("El vidrio es 100% reciclable", Icons.Outlined.LocalDrink, Color(0xFF00A550), "El vidrio se puede reciclar infinitas veces sin perder calidad. ¡Nunca lo tires a la basura normal!"),
        TipData("Lava los envases antes", Icons.Outlined.WaterDrop, Color(0xFF2979FF), "Un envase sucio puede contaminar todo un lote de reciclaje. Un enjuague rápido es suficiente."),
        TipData("Aplasta las botellas", Icons.Outlined.Compress, Color(0xFFFFB300), "Al aplastarlas ahorras espacio en el contenedor y el camión transporta más cantidad con menos viajes."),
        TipData("Tapas por separado", Icons.Outlined.Layers, Color(0xFFE91E63), "Las tapas suelen ser de un plástico diferente al de la botella. Sepáralas para facilitar el proceso.")
    )

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Ecotips", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(tips) { tip -> FlippableTipCard(tip) }
        }
    }
}

@Composable
fun FlippableTipCard(tip: TipData) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing), label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Transparent)
            .border(1.dp, tip.color.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { isFlipped = !isFlipped }
    ) {
        if (rotation <= 90f) FrontCardContent(tip) else Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) { BackCardContent(tip) }
    }
}

@Composable
fun FrontCardContent(tip: TipData) {
    Box(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(colors = listOf(tip.color, tip.color.copy(alpha = 0.8f))))) {
        Icon(imageVector = tip.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(100.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = tip.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Text(text = tip.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun BackCardContent(tip: TipData) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    Box(modifier = Modifier.fillMaxSize().background(surfaceColor).background(brush = Brush.verticalGradient(colors = listOf(tip.color.copy(alpha = 0.1f), tip.color.copy(alpha = 0.02f))))) {
        Icon(imageVector = tip.icon, contentDescription = null, tint = onSurfaceColor.copy(alpha = 0.08f), modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 30.dp, y = 30.dp))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(36.dp).background(onSurfaceColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = tip.color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = tip.description, color = onSurfaceColor.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 18.sp)
        }
    }
}

// --- 4. ACTIVIDAD RECIENTE ---
@Composable
fun RecentActivitySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Actividad Reciente", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Ver todo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
        }
        ActivityItem(Icons.Outlined.LocalDrink, "Botella Vidrio", "Hoy, 10:30", "+15 pts", Color(0xFF00A550))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.ShoppingBag, "Envase Yogur", "Ayer, 18:45", "+10 pts", Color(0xFFFFC107))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.Description, "Caja Cartón", "Ayer, 09:15", "+25 pts", Color(0xFF2979FF))
    }
}

@Composable
fun ActivityItem(icon: ImageVector, title: String, date: String, points: String, color: Color) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(text = points, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}