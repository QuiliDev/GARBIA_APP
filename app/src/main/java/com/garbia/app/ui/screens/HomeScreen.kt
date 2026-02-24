package com.garbia.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.garbia.app.ui.components.GarbiaTopBar

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val mainColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    // LÓGICA MÁGICA:
    // "derivedStateOf" optimiza el rendimiento.
    // Si el scroll es menor a 100 píxeles (estamos arriba), es TRUE (Visible).
    // Si bajamos más de 100 píxeles, es FALSE (Oculto).
    val showTopBar by remember {
        derivedStateOf { scrollState.value < 100 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        // 1. EL CONTENIDO (Con Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Cabecera grande
            HomeHeader(mainColor)

            // Dashboard (-40dp para solapar)
            DashboardStats(modifier = Modifier.offset(y = (-40).dp).padding(horizontal = 24.dp))

            Spacer(modifier = Modifier.height(10.dp).offset(y = (-40).dp))

            // Tips
            DidYouKnowSection(modifier = Modifier.offset(y = (-40).dp))

            // Actividad
            RecentActivitySection(modifier = Modifier.offset(y = (-40).dp))

            Spacer(modifier = Modifier.height(80.dp))
        }

        // 2. LA TOPBAR FLOTANTE (Animada)
        // La ponemos AL FINAL del Box para que flote encima de todo
        androidx.compose.animation.AnimatedVisibility(
            visible = showTopBar,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(), // Entra bajando
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(), // Sale subiendo
            modifier = Modifier.align(Alignment.TopCenter) // Fijada arriba
        ) {
            // Pasamos "true" o "false" según si estamos sobre el fondo verde o no.
            // Como aquí solo se muestra cuando estamos arriba (en lo verde), siempre será true.
            GarbiaTopBar(navController, isOverDarkBackground = true)
        }
    }
}

@Composable
fun HomeHeader(mainColor: Color) {
    // --- ANIMACIÓN DE FONDO ---
    val infiniteTransition = rememberInfiniteTransition(label = "background_anim")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing), RepeatMode.Reverse), label = "c1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -25f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse), label = "c2"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // 1. AUMENTAMOS LA ALTURA DEL FONDO VERDE
            // Antes 360.dp -> Ahora 430.dp
            // Necesitamos más altura porque vamos a empujar todo el texto hacia abajo.
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
        // --- DECORACIÓN CIRCULAR ---
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))) {
            Box(modifier = Modifier.offset(x = 120.dp + offset1.dp, y = (-60).dp + offset1.dp).size(250.dp).background(Color.White.copy(alpha = 0.08f), CircleShape))
            Box(modifier = Modifier.offset(x = (-40).dp + offset2.dp, y = 80.dp).size(160.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))
        }

        // --- CONTENIDO DE TEXTO ---
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                // Usamos statusBarsPadding para bajar lo que ocupa la hora/batería
                .statusBarsPadding()
                // 2. EMPUJAMOS EL TEXTO HACIA ABAJO
                // Antes 12.dp -> Ahora 80.dp
                // Esto crea el "hueco" necesario para que la TopBar flote sin tapar nada.
                .padding(top = 80.dp)
        ) {
            // Saludo
            Text(
                text = "Hola, Anthony 👋",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
            Text(
                text = "¡Vamos a cambiar el mundo hoy!",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Nivel y Puntos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🏆", fontSize = 14.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(text = "Nivel 5: Experto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "1200", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(text = " / 1500 XP", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
            }

            // Barra de Progreso
            Box(modifier = Modifier.fillMaxWidth().height(10.dp)) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)).background(Color.Black.copy(alpha = 0.2f)))
                Box(modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFC107)))
            }

            Text(
                text = "¡Estás a nada de subir de nivel!",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            // Botones Rápidos (Ranking, etc.)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(icon = Icons.Outlined.Leaderboard, label = "Ranking")
                QuickActionItem(icon = Icons.Outlined.CardGiftcard, label = "Premios")
                QuickActionItem(icon = Icons.Outlined.Lightbulb, label = "Tips")
                QuickActionItem(icon = Icons.Outlined.Map, label = "Mapa")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Sub-componente para los botones rápidos
@Composable
fun QuickActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(45.dp)
                // ✅ CORRECCIÓN IMPORTANTE:
                // 1. Primero recortamos la forma del círculo.
                .clip(CircleShape)
                // 2. Luego aplicamos el color de fondo (ya no hace falta decirle la forma aquí porque ya está recortado).
                .background(Color.White.copy(alpha = 0.15f))
                // 3. Por último, el clickable. Al estar después del clip, la onda respetará el círculo.
                .clickable { /* Acción */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DashboardStats(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(10.dp),
        border = androidx.compose.foundation.BorderStroke(
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
            modifier = Modifier
                .padding(vertical = 22.dp) // Quitamos padding horizontal para usar todo el ancho
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            // Ya no necesitamos horizontalArrangement aquí
        ) {
            // ZONA 1: Escaneos (Ocupa 1/3 del espacio)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.QrCodeScanner, targetValue = 50, suffix = "", label = "Escaneos")
            }

            // Divisor
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.Gray.copy(alpha = 0.2f)))

            // ZONA 2: Puntos (Ocupa 1/3 del espacio)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.MonetizationOn, targetValue = 1250, suffix = "", label = "Puntos", isPrimary = true)
            }

            // Divisor
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.Gray.copy(alpha = 0.2f)))

            // ZONA 3: Ahorrados (Ocupa 1/3 del espacio)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                StatItem(Icons.Outlined.Co2, targetValue = 5, suffix = "kg", label = "Ahorrados")
            }
        }
    }
}
// ✨ MEJORA 2: Componente para animar los números (Cuenta progresiva)
@Composable
fun StatItem(
    icon: ImageVector,
    targetValue: Int,
    suffix: String,
    label: String,
    isPrimary: Boolean = false
) {
    val color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    // Estado para la animación
    val counter = remember { androidx.compose.animation.core.Animatable(0f) }

    // Lanzamos la animación al iniciar
    androidx.compose.runtime.LaunchedEffect(key1 = targetValue) {
        counter.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))

        Spacer(modifier = Modifier.height(4.dp))

        // Texto con el número animado
        Text(
            text = "${counter.value.toInt()}$suffix",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp, // Un poco más grande
            color = color
        )

        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
// Asegúrate de que tu data class tenga este campo extra "description"
data class TipData(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val description: String // <--- NUEVO CAMPO
)
@Composable
fun DidYouKnowSection(modifier: Modifier = Modifier) {
    // AÑADIMOS LAS DESCRIPCIONES PARA LA "ESPALDA" DE LA CARTA
    val tips = listOf(
        TipData(
            "El vidrio es 100% reciclable",
            Icons.Outlined.LocalDrink,
            Color(0xFF00A550),
            "El vidrio se puede reciclar infinitas veces sin perder calidad. ¡Nunca lo tires a la basura normal!"
        ),
        TipData(
            "Lava los envases antes",
            Icons.Outlined.WaterDrop,
            Color(0xFF2979FF),
            "Un envase sucio puede contaminar todo un lote de reciclaje. Un enjuague rápido es suficiente."
        ),
        TipData(
            "Aplasta las botellas",
            Icons.Outlined.Compress,
            Color(0xFFFFB300),
            "Al aplastarlas ahorras espacio en el contenedor y el camión transporta más cantidad con menos viajes."
        ),
        TipData(
            "Tapas por separado",
            Icons.Outlined.Layers,
            Color(0xFFE91E63),
            "Las tapas suelen ser de un plástico diferente al de la botella. Sepáralas para facilitar el proceso."
        )
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ecotips",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tips) { tip ->
                FlippableTipCard(tip)
            }
        }
    }
}

@Composable
fun FlippableTipCard(tip: TipData) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "rotation"
    )

    Box( // Usamos Box para evitar sombras automáticas
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Transparent)
            .border(1.dp, tip.color.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { isFlipped = !isFlipped }
    ) {
        if (rotation <= 90f) {
            FrontCardContent(tip)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                BackCardContent(tip)
            }
        }
    }
}
// TU DISEÑO ORIGINAL (FRENTE)
@Composable
fun FrontCardContent(tip: TipData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(tip.color, tip.color.copy(alpha = 0.8f))
                )
            )
    ) {
        // Icono gigante decorativo
        Icon(
            imageVector = tip.icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono pequeño
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = tip.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            // Título
            Text(
                text = tip.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun BackCardContent(tip: TipData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // ✅ CORRECCIÓN: Primero pintamos de BLANCO sólido.
            // Esto evita que se vea oscura o transparente al girar.
            .background(Color.White)

            // LUEGO aplicamos el degradado de color encima
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tip.color.copy(alpha = 0.1f), // Un poco más de color arriba
                        tip.color.copy(alpha = 0.02f)
                    )
                )
            )
        // IMPORTANTE: El borde y el recorte van al final o en el contenedor padre,
        // pero si lo ponemos aquí aseguramos que la tarjeta tenga forma.
        // Como el padre (FlippableTipCard) ya recorta, aquí solo nos preocupamos del color.
    ) {
        // MARCA DE AGUA (Icono grande fondo)
        Icon(
            imageVector = tip.icon,
            contentDescription = null,
            tint = tip.color.copy(alpha = 0.08f), // Un pelín más visible
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = 30.dp)
        )

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Círculo del icono
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(tip.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = tip.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Texto
            Text(
                text = tip.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun RecentActivitySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)) {
        // 1. CABECERA MEJORADA: Título + Botón "Ver todo"
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Actividad Reciente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Ver todo",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Navegar al historial */ }
            )
        }

        // 2. LISTA DE TARJETAS
        ActivityItem(Icons.Outlined.LocalDrink, "Botella Vidrio", "Hoy, 10:30", "+15 pts", Color(0xFF00A550))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.ShoppingBag, "Envase Yogur", "Ayer, 18:45", "+10 pts", Color(0xFFFFC107))
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(Icons.Outlined.Description, "Caja Cartón", "Ayer, 09:15", "+25 pts", Color(0xFF2979FF))
    }
}

@Composable
fun ActivityItem(icon: ImageVector, title: String, date: String, points: String, color: Color) {
    // Usamos CARD en lugar de Row plano para darle elevación y presencia
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp), // Sombra sutil
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo circular suave
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Puntos destacados
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = points,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}