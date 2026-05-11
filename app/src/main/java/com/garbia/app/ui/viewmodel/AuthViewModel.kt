package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.remote.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EstadoAuth {
    object Cargando     : EstadoAuth()
    object Local        : EstadoAuth()
    data class Autenticado(val uid: String) : EstadoAuth()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoAuth>(EstadoAuth.Cargando)
    val estado: StateFlow<EstadoAuth> = _estado

    init {
        autenticar()
    }

    private fun autenticar() {
        viewModelScope.launch {
            val uid = authService.signInAnonymously()
            if (uid != null) {
                usuarioDao.actualizarFirebaseUid(uid)
                _estado.value = EstadoAuth.Autenticado(uid)
            } else {
                _estado.value = EstadoAuth.Local
            }
        }
    }

    fun cerrarSesion() {
        authService.signOut()
        _estado.value = EstadoAuth.Local
    }
}
