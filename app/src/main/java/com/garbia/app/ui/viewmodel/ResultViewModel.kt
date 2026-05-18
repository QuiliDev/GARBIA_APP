package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.model.ResultadoEscaneo
import com.garbia.app.data.repository.EscaneoResultHolder
import com.garbia.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val resultHolder: EscaneoResultHolder
) : ViewModel() {

    private var escaneoGuardado = false

    fun getResultado(): ResultadoEscaneo =
        resultHolder.resultado ?: ResultadoEscaneo(identificado = false)

    fun confirmarEscaneo(resultado: ResultadoEscaneo, onGuardado: () -> Unit) {
        if (!resultado.identificado || escaneoGuardado) {
            onGuardado()
            return
        }
        escaneoGuardado = true
        viewModelScope.launch {
            usuarioRepository.registrarEscaneo(
                tipoMaterial = resultado.tipoMaterial,
                contenedor   = resultado.contenedor,
                puntos       = resultado.puntos,
                co2          = resultado.co2Ahorrado
            )
            resultHolder.resultado = null
            onGuardado()
        }
    }
}
