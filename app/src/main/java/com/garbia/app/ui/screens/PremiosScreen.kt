package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.ui.viewmodel.PremiosViewModel

// --- MODELO ---
data class Premio(
    val title: String,
    val description: String,
    val pointsCost: Int,
    val icon: ImageVector,
    val color: Color,
    val category: String
)

// --- DATOS ESTÁTICOS ---
private val premiosData = listOf(
    Premio("Café Gratis",        "Un café de cortesía en cualquier EcoBar colaborador. ¡Te lo mereces!",                    300,   Icons.Outlined.LocalCafe,        Color(0xFF795548), "Experiencias"),
    Premio("Bolsa Reutilizable", "Bolsa de tela orgánica 100% con el logo de Garbia y frases eco.",                         500,   Icons.Outlined.ShoppingBag,      Color(0xFF00A550), "Productos"),
    Premio("Descuento 10%",      "Aplica un 10% de descuento en tu próxima compra en la Ecotienda online.",                  800,   Icons.Outlined.LocalOffer,       Color(0xFF2979FF), "Descuentos"),
    Premio("Kit Hogar Eco",      "Kit completo: cubo de reciclaje de 3 compartimentos, bolsas compostables y guía de uso.", 1_200, Icons.Outlined.Home,             Color(0xFFFF9800), "Productos"),
    Premio("Descuento 20%",      "20% de descuento en toda la tienda EcoShop durante 7 días consecutivos.",                 1_500, Icons.Outlined.Sell,             Color(0xFFE91E63), "Descuentos"),
    Premio("Árbol Plantado",     "Plantamos un árbol en tu nombre en zonas de reforestación activa en España.",             2_000, Icons.Outlined.Park,             Color(0xFF4CAF50), "Impacto"),
    Premio("Taller Reciclaje",   "Acceso gratuito a un taller online de reciclaje avanzado con certificado.",               2_500, Icons.Outlined.School,           Color(0xFF9C27B0), "Experiencias"),
    Premio("Mochila Eco",        "Mochila fabricada con plástico reciclado del océano. Edición limitada.",                  3_000, Icons.Outlined.Backpack,          Color(0xFF00BCD4), "Productos"),
)

private val purple     = Color(0xFF7C3AED)
private val purpleDark = Color(0xFF4C1D95)

private val categories = listOf("Todos", "Disponibles", "Descuentos", "Productos", "Experiencias", "Impacto")

// --- PANTALLA ---
@Composable
fun PremiosScreen(
    navController: NavController,
    viewModel: PremiosViewModel = hiltViewModel()
) {
    val userPoints by viewModel.puntos.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("Todos") }

    val filtered = when (selectedCategory) {
        "Disponibles" -> premiosData.filter { userPoints >= it.pointsCost }
        "Todos"       -> premiosData
        else          -> premiosData.filter { it.category == selectedCategory }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── CABECERA ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.linearGradient(listOf(purple, purpleDark)),
                            RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                ) {
                    Box(Modifier.offset(180.dp, (-40).dp).size(220.dp).background(Color.White.copy(.07f), CircleShape))
                    Box(Modifier.offset((-50).dp, 80.dp).size(150.dp).background(Color.White.copy(.05f), CircleShape))

                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Outlined.ArrowBackIosNew, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Icon(Icons.Outlined.CardGiftcard, null, tint = Color.White, modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Premios", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        }

                        Spacer(Modifier.height(16.dp))

                        // Saldo de puntos
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(.18f)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(50.dp).background(Color(0xFFFFC107).copy(.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                                }
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text("Tus puntos disponibles", color = Color.White.copy(.8f), fontSize = 13.sp)
                                    Text("$userPoints pts", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp, letterSpacing = (-0.5).sp)
                                }
                                Spacer(Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Canjeados", color = Color.White.copy(.7f), fontSize = 11.sp)
                                    Text("3 premios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ── FILTROS ───────────────────────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val selected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .border(
                                    1.dp,
                                    if (selected) purple else MaterialTheme.colorScheme.outline.copy(.3f),
                                    RoundedCornerShape(50)
                                ),
                            shape = RoundedCornerShape(50),
                            color = if (selected) purple else MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                cat,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface.copy(.7f),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // ── TARJETAS DE PREMIOS ───────────────────────────────────
            items(filtered) { prize ->
                PrizeCard(prize = prize, userPoints = userPoints, accentColor = purple)
                Spacer(Modifier.height(12.dp))
            }

            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}

// --- TARJETA PREMIO ---
@Composable
private fun PrizeCard(prize: Premio, userPoints: Int, accentColor: Color) {
    val canRedeem = userPoints >= prize.pointsCost
    val remaining = prize.pointsCost - userPoints

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canRedeem) MaterialTheme.colorScheme.surface
                             else MaterialTheme.colorScheme.surface.copy(.6f)
        ),
        elevation = CardDefaults.cardElevation(if (canRedeem) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono
                Box(
                    Modifier
                        .size(60.dp)
                        .background(prize.color.copy(if (canRedeem) .15f else .07f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        prize.icon, null,
                        tint = if (canRedeem) prize.color else prize.color.copy(.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            prize.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (canRedeem) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurface.copy(.5f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = prize.color.copy(if (canRedeem) .12f else .06f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                prize.category, fontSize = 10.sp,
                                color = if (canRedeem) prize.color else prize.color.copy(.4f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        prize.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(if (canRedeem) .6f else .35f),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(.1f))
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Coste en puntos
                Column(Modifier.weight(1f)) {
                    Text("Coste", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${prize.pointsCost}",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = if (canRedeem) prize.color else MaterialTheme.colorScheme.onSurface.copy(.4f)
                        )
                        Text(
                            " pts",
                            fontSize = 13.sp,
                            color = if (canRedeem) prize.color.copy(.7f) else Color.Gray.copy(.5f),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
                // Botón
                Button(
                    onClick = {},
                    enabled = canRedeem,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = prize.color,
                        disabledContainerColor = MaterialTheme.colorScheme.outline.copy(.15f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    if (canRedeem) {
                        Icon(Icons.Outlined.Redeem, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Canjear", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Outlined.Lock, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Faltan $remaining pts", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
