package com.garbia.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.garbia.app.data.model.ResultadoEscaneo
import com.garbia.app.ui.viewmodel.ResultViewModel

// --- 1. MODELO UI ---
data class RecycleResultData(
    val themeColor: Color,
    val containerName: String,
    val containerType: String,
    val description: String,
    val points: Int,
    val co2Ahorrado: Float,
    val mainIcon: ImageVector,
    val congratsMessage: String
)

// --- 2. PANTALLA PRINCIPAL ---
@Composable
fun ResultScreen(
    navController: NavController,
    photoUri: String,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val resultado = remember { viewModel.getResultado() }
    val displayData = remember(resultado) { resultado.toDisplayData() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (displayData == null) {
            NoIdentifiedResultView(navController)
        } else {
            SuccessfulResultView(
                navController = navController,
                data = displayData,
                onContinuar = {
                    viewModel.confirmarEscaneo(resultado)
                    navController.navigate("home_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun ResultadoEscaneo.toDisplayData(): RecycleResultData? {
    if (!identificado) return null
    return when (tipoMaterial.lowercase()) {
        "vidrio" -> RecycleResultData(Color(0xFF00A550), contenedor, tipoMaterial.uppercase(), descripcion, puntos, co2Ahorrado, Icons.Outlined.LocalDrink,   "¡Excelente reciclaje!")
        "plástico", "plastico", "envases" -> RecycleResultData(Color(0xFFFFC107), contenedor, tipoMaterial.uppercase(), descripcion, puntos, co2Ahorrado, Icons.Outlined.ShoppingBag, "¡Bien hecho!")
        "papel", "cartón", "carton" -> RecycleResultData(Color(0xFF2979FF), contenedor, tipoMaterial.uppercase(), descripcion, puntos, co2Ahorrado, Icons.Outlined.Description,  "¡Perfecto!")
        "orgánico", "organico" -> RecycleResultData(Color(0xFF8BC34A), contenedor, tipoMaterial.uppercase(), descripcion, puntos, co2Ahorrado, Icons.Outlined.Grass,        "¡Genial!")
        else -> RecycleResultData(Color(0xFF607D8B), contenedor, tipoMaterial.uppercase(), descripcion, puntos, co2Ahorrado, Icons.Outlined.Delete, "¡Buen trabajo!")
    }
}

// --- 3. VISTA ÉXITO ---
@Composable
fun SuccessfulResultView(
    navController: NavController,
    data: RecycleResultData,
    onContinuar: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(data.themeColor, data.themeColor.copy(alpha = 0.6f))))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.offset(x = (-50).dp, y = (-50).dp).size(200.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = 50.dp, y = 100.dp).size(150.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
        }

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(text = data.congratsMessage.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 2.sp)

            Spacer(modifier = Modifier.height(40.dp))

            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(160.dp).scale(scale).background(Color.White.copy(alpha = 0.2f), CircleShape))
                Box(modifier = Modifier.size(120.dp).background(Color.White, CircleShape).shadow(20.dp, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(imageVector = data.mainIcon, contentDescription = null, tint = data.themeColor, modifier = Modifier.size(64.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(color = data.themeColor.copy(alpha = 0.1f), shape = RoundedCornerShape(50), modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = data.containerType, color = data.themeColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                    }

                    Text(text = data.containerName, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = data.description, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), lineHeight = 24.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)).padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatColumn(points = "+${data.points}", label = "Puntos", color = data.themeColor)
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.2f)))
                        StatColumn(points = "${data.co2Ahorrado}kg", label = "CO₂ Ahorrado", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onContinuar,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = data.themeColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatColumn(points: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = points, fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

// --- 4. VISTA ERROR ---
@Composable
fun NoIdentifiedResultView(navController: NavController) {
    val darkBg    = Color(0xFF1A1C1E)
    val cardBg    = Color(0xFF2B2D30)
    val accentColor = Color(0xFFFF6F00)

    Box(modifier = Modifier.fillMaxSize().background(darkBg)) {
        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = (-100).dp).size(300.dp).background(accentColor.copy(alpha = 0.05f), CircleShape))

        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))

            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(120.dp).border(2.dp, accentColor.copy(alpha = 0.3f), CircleShape))
                Box(modifier = Modifier.size(90.dp).background(accentColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.QuestionMark, contentDescription = null, tint = accentColor, modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "No identificado", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "No hemos podido reconocer el objeto con seguridad. La IA necesita una imagen más clara.", textAlign = TextAlign.Center, color = Color.White.copy(alpha = 0.7f), lineHeight = 24.sp)

            Spacer(modifier = Modifier.height(40.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Consejos para mejorar:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    TipRow("Limpia la lente de la cámara")
                    TipRow("Busca un lugar con buena luz")
                    TipRow("Enfoca el objeto completo")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) { Text("Reportar") }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Probar otra vez")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TipRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(6.dp).background(Color(0xFFFF6F00), CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
    }
}
