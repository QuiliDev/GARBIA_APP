package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupNombreViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    fun guardarNombre(nombre: String, onDone: () -> Unit) {
        viewModelScope.launch {
            usuarioDao.actualizarNombre(nombre.trim())
            appPreferences.setHasSetupNombre(true)
            onDone()
        }
    }
}
