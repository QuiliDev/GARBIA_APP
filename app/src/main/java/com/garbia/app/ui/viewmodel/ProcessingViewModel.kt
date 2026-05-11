package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.remote.ApiService
import com.garbia.app.data.repository.EscaneoResultHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessingViewModel @Inject constructor(
    private val apiService: ApiService,
    private val resultHolder: EscaneoResultHolder
) : ViewModel() {

    private val _statusText  = MutableStateFlow("Preparando imagen…")
    val statusText: StateFlow<String> = _statusText

    private val _analisisDone = MutableStateFlow(false)
    val analisisDone: StateFlow<Boolean> = _analisisDone

    fun analizarFoto(photoUri: String) {
        if (_analisisDone.value) return
        viewModelScope.launch {
            _statusText.value = "Preparando imagen…"
            delay(800)
            _statusText.value = "Enviando al modelo IA…"
            delay(1_200)
            _statusText.value = "Analizando material…"

            val resultado = apiService.clasificarImagen(photoUri)
            resultHolder.resultado = resultado

            _statusText.value = when {
                resultado.isFromCache  -> "📦 Sin conexión — resultado del caché local"
                resultado.identificado -> "✓ ${resultado.tipoMaterial} identificado"
                else                   -> "No se pudo identificar el material"
            }

            delay(400)
            _analisisDone.value = true
        }
    }
}
