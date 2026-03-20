package com.garbia.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 1. CABECERA ANIMADA ---
@Composable
fun ProfileHeader(
    username: String = "Anthony", // Parametrizable
    level: String = "Nivel 5: Experto"
) {
    val mainColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition(label = "profile_bg")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse), label = "scale"
    )

    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 80f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "moveY"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(mainColor, Color(0xFF004D40))
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
    ) {
        // Decoración de fondo
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp + moveY.dp)
                    .size(200.dp)
                    .scale(scale)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-80).dp, y = 50.dp - moveY.dp)
                    .size(250.dp)
                    .scale(scale * 0.8f)
                    .background(Color.White.copy(alpha = 0.03f), CircleShape)
            )
        }

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(4.dp, Color.White.copy(alpha = 0.2f)),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(36.dp).clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Edit, "Editar", tint = mainColor, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = username, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "🏆 $level",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// --- 2. TARJETA DE ESTADÍSTICAS ---
@Composable
fun ProfileStatsCard(
    puntos: Int = 0,
    escaneos: Int = 0,
    co2: Float = 0f,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileStatItem(Modifier.weight(1f), "%,d".format(puntos), "Puntos", Icons.Outlined.MonetizationOn, Color(0xFFFFC107))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.2f)))
            ProfileStatItem(Modifier.weight(1f), "$escaneos", "Objetos", Icons.Outlined.Recycling, Color(0xFF00A550))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.2f)))
            ProfileStatItem(Modifier.weight(1f), "%.1fkg".format(co2), "CO2 Saved", Icons.Outlined.Cloud, Color(0xFF2979FF))
        }
    }
}

@Composable
private fun ProfileStatItem( // Private porque solo se usa dentro de la Card
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

// --- 3. ELEMENTOS DE MENÚ ---
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 28.dp, bottom = 8.dp, top = 16.dp)
    )
}

@Composable
fun ProfileMenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f))
    }
}