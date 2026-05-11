package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiosViewModel @Inject constructor(
    usuarioRepository: UsuarioRepository,
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    val puntos: StateFlow<Int> = usuarioRepository.getUsuario()
        .map { it?.puntosTotales ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _canjesRealizados = MutableStateFlow(0)
    val canjesRealizados: StateFlow<Int> = _canjesRealizados

    fun canjearPremio(coste: Int) {
        viewModelScope.launch {
            usuarioDao.restarPuntos(coste)
            _canjesRealizados.value++
        }
    }
}
