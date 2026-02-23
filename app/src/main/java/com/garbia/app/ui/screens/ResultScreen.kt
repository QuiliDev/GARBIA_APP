package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- 1. MODELO DE DATOS (Para resultados exitosos) ---
data class RecycleResultData(
    val themeColor: Color,
    val containerName: String,
    val description: String,
    val points: Int,
    val mainIcon: ImageVector,
    val binIcon: ImageVector,
    val congratsMessage: String
)

// --- 2. FUNCIÓN PRINCIPAL ---
@Composable
fun ResultScreen(navController: NavController, photoUri: String) {
    // Definición de los resultados posibles
    val glassResult = RecycleResultData(
        themeColor = Color(0xFF00A550), containerName = "CONTENEDOR\nVERDE", description = "Botellas, tarros y frascos de vidrio", points = 15, mainIcon = Icons.Outlined.LocalDrink, binIcon = Icons.Outlined.Delete, congratsMessage = "¡Excelente!"
    )
    val plasticResult = RecycleResultData(
        themeColor = Color(0xFFFFC107), containerName = "CONTENEDOR\nAMARILLO", description = "Envases de plástico, latas y briks", points = 10, mainIcon = Icons.Outlined.LocalDrink, binIcon = Icons.Outlined.Delete, congratsMessage = "¡Bien hecho!"
    )
    val organicResult = RecycleResultData(
        themeColor = Color(0xFF795548), containerName = "CONTENEDOR\nMARRÓN", description = "Restos de comida y desechos orgánicos", points = 20, mainIcon = Icons.Outlined.Grass, binIcon = Icons.Outlined.Delete, congratsMessage = "¡Genial!"
    )
    val paperResult = RecycleResultData(
        themeColor = Color(0xFF2979FF), containerName = "CONTENEDOR\nAZUL", description = "Papel y cartón, cajas y periódicos", points = 25, mainIcon = Icons.Outlined.Description, binIcon = Icons.Outlined.Delete, congratsMessage = "¡Perfecto!"
    )

    // SIMULACIÓN IA MEJORADA: Añadimos 'null' a la lista para simular el fallo
    val result = remember {
        listOf(glassResult, plasticResult, organicResult, paperResult, null).random()
    }

    // EL PORTERO: Decide qué pantalla mostrar
    if (result == null) {
        // Mostrar pantalla de "No Identificado"
        NoIdentifiedResultView(navController = navController)
    } else {
        // Mostrar pantalla de Resultado Exitoso (pasándole los datos)
        SuccessfulResultView(navController = navController, data = result)
    }
}

// --- 3. VISTA DE RESULTADO EXITOSO (El diseño colorido anterior) ---
@Composable
fun SuccessfulResultView(navController: NavController, data: RecycleResultData) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Parte Superior (Color)
        Box(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxWidth()
                .background(
                    color = data.themeColor,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("RESULTADO", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp))
                Icon(imageVector = data.mainIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = data.containerName, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = data.description, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
            }
        }

        // Parte Inferior (Blanco)
        Column(
            modifier = Modifier.weight(0.45f).fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = data.binIcon, contentDescription = "Cubo", tint = data.themeColor, modifier = Modifier.size(80.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFC107), CircleShape), contentAlignment = Alignment.Center) {
                        Text("$", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = data.congratsMessage, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "+${data.points} Puntos GarbiA", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("home_screen") { popUpTo("home_screen") { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = data.themeColor), shape = RoundedCornerShape(28.dp)
            ) {
                Text("Aceptar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "¿No es correcto? Reportar error", color = Color(0xFF008F7A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { })
        }
    }
}

// --- 4. NUEVA VISTA: NO IDENTIFICADO (Diseño Oscuro/Naranja) ---
@Composable
fun NoIdentifiedResultView(navController: NavController) {
    // Colores
    val darkBackgroundTop = Color(0xFF3E4147)
    val darkBackgroundBottom = Color(0xFF2C2E33) // El color oscuro de abajo
    val orangeAccent = Color(0xFFFF6F00)
    val cardBackground = Color(0xFF3E4147)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackgroundBottom)
    ) {
        // --- Parte Superior (Gris Oscuro) ---
        Box(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxWidth()
                .background(
                    color = darkBackgroundTop,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Icono de Interrogación Grande
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    contentDescription = null,
                    tint = orangeAccent,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Título Naranja
                Text(
                    text = "NO IDENTIFICADO",
                    color = orangeAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Descripción Blanca
                Text(
                    text = "No pudimos reconocer el objeto.\nIntenta de nuevo.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        // --- Parte Inferior (Más Oscura) ---
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxWidth()
                // .background(darkBackgroundBottom) <-- Ya no es estrictamente necesario porque el padre lo tiene, pero se puede dejar.
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. IMAGEN DEMO
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Demo Imagen",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(60.dp)
                )
            }

            // 2. Tarjeta de "0 Puntos"
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.MonetizationOn,
                        contentDescription = "Error puntos",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "¡Ups! 0 Puntos.", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Text(text = "Intenta otra vez.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Botón REINTENTAR
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Reintentar foto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // 4. Reportar
            Text(
                text = "Reportar problema",
                color = orangeAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
    }
}