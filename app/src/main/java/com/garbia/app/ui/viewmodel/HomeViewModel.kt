package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.model.Escaneo
import com.garbia.app.data.repository.RachaRepository
import com.garbia.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val nombre: String = "Usuario",
    val nivelLabel: String = "Nivel 1: Principiante",
    val puntosEnNivel: Int = 0,
    val puntosParaSiguienteNivel: Int = 500,
    val escaneosTotales: Int = 0,
    val puntosTotales: Int = 0,
    val co2Ahorrado: Float = 0f,
    val historialReciente: List<Escaneo> = emptyList(),
    val rachaActual: Int = 0,
    val rachaMáxima: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val rachaRepository: RachaRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        usuarioRepository.getUsuario(),
        usuarioRepository.getHistorialReciente(),
        rachaRepository.getRacha()
    ) { usuario, historial, racha ->
        if (usuario == null) HomeUiState()
        else HomeUiState(
            nombre                   = usuario.nombre,
            nivelLabel               = usuario.nivelLabel,
            puntosEnNivel            = usuario.puntosEnNivel,
            puntosParaSiguienteNivel = usuario.puntosParaSiguienteNivel,
            escaneosTotales          = usuario.escaneosTotales,
            puntosTotales            = usuario.puntosTotales,
            co2Ahorrado              = usuario.co2Ahorrado,
            historialReciente        = historial,
            rachaActual              = racha.actual,
            rachaMáxima              = racha.maxima
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            usuarioRepository.inicializarSiNecesario()
        }
    }
}
