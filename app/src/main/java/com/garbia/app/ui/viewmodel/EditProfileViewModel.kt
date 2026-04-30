package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    val nombreActual: StateFlow<String> = usuarioRepository.getUsuario()
        .map { it?.nombre ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun guardarNombre(nombre: String) {
        val limpio = nombre.trim().take(30)
        if (limpio.isBlank()) return
        viewModelScope.launch { usuarioDao.actualizarNombre(limpio) }
    }
}
