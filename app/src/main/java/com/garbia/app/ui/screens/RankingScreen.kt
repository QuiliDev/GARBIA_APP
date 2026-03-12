package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.data.model.RankingEntry
import com.garbia.app.ui.viewmodel.RankingViewModel

private val amber     = Color(0xFFF59E0B)
private val amberDark = Color(0xFFB45309)
private val gold      = Color(0xFFFFD700)
private val silver    = Color(0xFFC0C0C0)
private val bronze    = Color(0xFFCD7F32)

@Composable
fun RankingScreen(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentUser = uiState.entries.firstOrNull { it.isCurrentUser }
        ?: RankingEntry(0, "Tú", "TÚ", 0, "Nivel 1: Novato", true)

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── CABECERA ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.linearGradient(listOf(amber, amberDark)),
                            RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                ) {
                    Box(Modifier.offset(200.dp, (-30).dp).size(200.dp).background(Color.White.copy(.07f), CircleShape))
                    Box(Modifier.offset((-40).dp, 90.dp).size(130.dp).background(Color.White.copy(.05f), CircleShape))

                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Outlined.ArrowBackIosNew, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Icon(Icons.Outlined.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ranking Global", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        }

                        Spacer(Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(.18f)),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(46.dp).background(Color.White.copy(.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(currentUser.initials, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Tu posición actual", color = Color.White.copy(.8f), fontSize = 12.sp)
                                    Text(currentUser.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(currentUser.level, color = Color.White.copy(.7f), fontSize = 12.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    val posText = if (currentUser.position > 0) "#${currentUser.position}" else "-"
                                    Text(posText, color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                                    Text("${currentUser.points} pts", color = Color.White.copy(.8f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ── LOADING / ERROR ────────────────────────────────────────
            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = amber)
                    }
                }
            } else if (uiState.error != null) {
                item {
                    Text(
                        "Error al cargar el ranking",
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // ── PODIO ──────────────────────────────────────────────
                if (uiState.entries.size >= 3) {
                    item { PodiumSection(uiState.entries.take(3)) }
                }

                // ── TÍTULO LISTA ───────────────────────────────────────
                item {
                    Text(
                        "Clasificación completa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // ── FILAS ──────────────────────────────────────────────
                itemsIndexed(uiState.entries) { idx, entry ->
                    RankingRow(entry)
                    if (idx < uiState.entries.lastIndex) Spacer(Modifier.height(8.dp))
                }
            }

            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}

@Composable
private fun PodiumSection(top3: List<RankingEntry>) {
    val medalColor = listOf(gold, silver, bronze)
    val medals     = listOf("🥇", "🥈", "🥉")
    val heights: List<Dp> = listOf(110.dp, 80.dp, 65.dp)
    val order      = listOf(1, 0, 2)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        order.forEach { idx ->
            val entry = top3[idx]
            val color = medalColor[idx]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(medals[idx], fontSize = 26.sp)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .size(56.dp)
                        .background(color.copy(.15f), CircleShape)
                        .border(2.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(entry.initials, fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(entry.name.split(" ").first(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                Text("${entry.points} pts", fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .width(80.dp)
                        .height(heights[idx])
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(color.copy(.35f))
                )
            }
        }
    }
}

@Composable
private fun RankingRow(entry: RankingEntry) {
    val posColor = when (entry.position) {
        1    -> gold
        2    -> silver
        3    -> bronze
        else -> MaterialTheme.colorScheme.onSurface.copy(.4f)
    }
    val isMe = entry.isCurrentUser

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(if (isMe) Modifier.border(1.5.dp, amber, RoundedCornerShape(16.dp)) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) amber.copy(.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isMe) 6.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(34.dp), contentAlignment = Alignment.Center) {
                Text("#${entry.position}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = posColor)
            }
            Box(
                Modifier.size(42.dp).background(
                    if (isMe) amber.copy(.2f) else MaterialTheme.colorScheme.primary.copy(.1f),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    entry.initials,
                    fontWeight = FontWeight.Bold,
                    color = if (isMe) amber else MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (isMe) {
                        Spacer(Modifier.width(6.dp))
                        Surface(color = amber.copy(.15f), shape = RoundedCornerShape(6.dp)) {
                            Text("Tú", fontSize = 10.sp, color = amber, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(entry.level, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(.5f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${entry.points}", fontWeight = FontWeight.Black, fontSize = 17.sp,
                    color = if (isMe) amber else MaterialTheme.colorScheme.onSurface)
                Text("puntos", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
