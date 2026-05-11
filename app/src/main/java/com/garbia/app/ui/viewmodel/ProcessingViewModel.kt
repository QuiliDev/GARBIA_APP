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

    private val _statusText = MutableStateFlow("Subiendo imagen...")
    val statusText: StateFlow<String> = _statusText

    private val _analisisDone = MutableStateFlow(false)
    val analisisDone: StateFlow<Boolean> = _analisisDone

    fun analizarFoto(photoUri: String) {
        if (_analisisDone.value) return
        viewModelScope.launch {
            _statusText.value = "Subiendo imagen..."
            delay(1500)
            _statusText.value = "Consultando modelo IA..."
            delay(1500)
            _statusText.value = "Identificando material..."

            val resultado = apiService.clasificarImagen(photoUri)
            resultHolder.resultado = resultado

            delay(500)
            _analisisDone.value = true
        }
    }
}
