package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.model.Escaneo
import com.garbia.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val escaneoDao: EscaneoDao
) : ViewModel() {

    private val _filtro = MutableStateFlow<String?>(null)
    val filtro: StateFlow<String?> = _filtro

    val escaneos: StateFlow<List<Escaneo>> = combine(
        escaneoDao.getAllEscaneos(),
        _filtro
    ) { lista, tipo ->
        val dominio = lista.map {
            Escaneo(
                id              = it.id,
                tipoMaterial    = it.tipoMaterial,
                contenedor      = it.contenedor,
                puntosGanados   = it.puntosGanados,
                co2Ahorrado     = it.co2Ahorrado,
                fechaTimestamp  = it.fechaTimestamp
            )
        }
        if (tipo == null) dominio else dominio.filter { it.tipoMaterial.equals(tipo, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFiltro(tipo: String?) { _filtro.value = tipo }
}
