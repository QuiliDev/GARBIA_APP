package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val mainColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(scrollState)
    ) {
        // 1. CABECERA INNOVADORA CON BÚSQUEDA Y NIVEL
        HomeHeader(mainColor)

        // 2. DASHBOARD FLOTANTE (Estadísticas rápidas)
        // Lo subimos un poco (-40.dp) para que se superponga a la cabecera (Efecto moderno)
        DashboardStats(modifier = Modifier.offset(y = (-40).dp).padding(horizontal = 24.dp))

        // Ajustamos el espacio porque subimos el dashboard
        Spacer(modifier = Modifier.height(10.dp).offset(y = (-40).dp))

        // 3. CARRUSEL "SABÍAS QUE..."
        DidYouKnowSection(modifier = Modifier.offset(y = (-40).dp))

        // 4. ACTIVIDAD RECIENTE
        RecentActivitySection(modifier = Modifier.offset(y = (-40).dp))

        // Espacio final para el BottomNav
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun HomeHeader(mainColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Altura generosa
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(mainColor, mainColor.copy(alpha = 0.8f))
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            // Fila Superior: Saludo y Notificaciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, Anthony 👋",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¡A reciclar se ha dicho!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                // Botón de notificación
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BARRA DE NIVEL GAMIFICADA
            Text(
                text = "Nivel 5: Reciclador Experto",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Barra de progreso custom
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFFC107), // Amarillo "Gold" para progreso
                trackColor = Color.White.copy(alpha = 0.3f),
            )
            Text(
                text = "Faltan 300 puntos para Nivel Maestro",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun DashboardStats(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(Icons.Outlined.QrCodeScanner, "50", "Escaneos")
            // Divisor vertical
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.2f)))
            StatItem(Icons.Outlined.MonetizationOn, "1250", "Puntos", isPrimary = true)
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.2f)))
            StatItem(Icons.Outlined.Co2, "5kg", "Ahorrados")
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, isPrimary: Boolean = false) {
    val color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

data class TipData(val title: String, val icon: ImageVector, val color: Color)

@Composable
fun DidYouKnowSection(modifier: Modifier = Modifier) {
    val tips = listOf(
        TipData("El vidrio es 100% reciclable", Icons.Outlined.LocalDrink, Color(0xFF00A550)),
        TipData("Lava los envases antes", Icons.Outlined.WaterDrop, Color(0xFF2979FF)),
        TipData("Aplasta las botellas", Icons.Outlined.Compress, Color(0xFFFFC107))
    )

    Column(modifier = modifier) {
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "¿Sabías que?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tips) { tip ->
                TipCard(tip)
            }
        }
    }
}

@Composable
fun TipCard(tip: TipData) {
    Card(
        modifier = Modifier.size(width = 160.dp, height = 180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tip.color.copy(alpha = 0.1f)) // Fondo suave
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = tip.icon, contentDescription = null, tint = tip.color)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = tip.title,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RecentActivitySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
        Text(
            text = "Actividad Reciente",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de items de ejemplo
        ActivityItem(Icons.Outlined.LocalDrink, "Botella Vidrio", "Hoy, 10:30", "+15 pts", Color(0xFF00A550))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.ShoppingBag, "Envase Yogur", "Ayer, 18:45", "+10 pts", Color(0xFFFFC107))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.Description, "Caja Cartón", "Ayer, 09:15", "+25 pts", Color(0xFF2979FF))
    }
}

@Composable
fun ActivityItem(icon: ImageVector, title: String, date: String, points: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = date, fontSize = 12.sp, color = Color.Gray)
        }

        Text(
            text = points,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}