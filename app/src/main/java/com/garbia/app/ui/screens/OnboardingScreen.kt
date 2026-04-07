package com.garbia.app.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.garbia.app.ui.Screen
import com.garbia.app.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val gradientTop: Color,
    val gradientBottom: Color
)

private val onboardingPages = listOf(
    OnboardingPage(
        emoji = "🤳",
        title = "Escanea y Aprende",
        description = "Apunta la cámara a cualquier residuo. Nuestra IA te dice exactamente en qué contenedor va y por qué.",
        gradientTop = Color(0xFF1B5E20),
        gradientBottom = Color(0xFF002904)
    ),
    OnboardingPage(
        emoji = "🏆",
        title = "Gana Puntos y Sube de Nivel",
        description = "Cada escaneo correcto suma puntos, subes de nivel y desbloqueas logros. ¡El reciclaje nunca había sido tan adictivo!",
        gradientTop = Color(0xFF0D47A1),
        gradientBottom = Color(0xFF000a1e)
    ),
    OnboardingPage(
        emoji = "🌍",
        title = "Cuida el Planeta",
        description = "Cada objeto reciclado reduce el CO₂. Compite en el ranking global y haz del reciclaje un hábito diario.",
        gradientTop = Color(0xFF004D40),
        gradientBottom = Color(0xFF00251a)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()

    fun done() {
        viewModel.finishOnboarding()
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Onboarding.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            val page = onboardingPages[index]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(page.gradientTop, page.gradientBottom)))
                    .statusBarsPadding()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(page.emoji, fontSize = 96.sp)
                Spacer(Modifier.height(48.dp))
                Text(
                    page.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    page.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(onboardingPages.size) { i ->
                    val width by animateDpAsState(
                        targetValue = if (i == pagerState.currentPage) 28.dp else 8.dp,
                        label = "dot_$i"
                    )
                    Box(
                        Modifier
                            .width(width)
                            .height(8.dp)
                            .background(
                                if (i == pagerState.currentPage) Color.White else Color.White.copy(0.4f),
                                CircleShape
                            )
                    )
                }
            }
            Spacer(Modifier.height(32.dp))

            val isLast = pagerState.currentPage == onboardingPages.lastIndex
            Button(
                onClick = {
                    if (isLast) done()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = onboardingPages[pagerState.currentPage].gradientTop
                )
            ) {
                Text(
                    if (isLast) "¡Empezar a Reciclar!" else "Siguiente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (!isLast) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Outlined.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }

            if (!isLast) {
                TextButton(onClick = ::done) {
                    Text("Saltar", color = Color.White.copy(0.7f), fontSize = 14.sp)
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}
