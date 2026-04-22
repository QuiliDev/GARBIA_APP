package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.local.dao.EscaneoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DiaEstadistica(
    val etiqueta: String,   // "Lun", "Mar"...
    val escaneos: Int,
    val puntos: Int
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val escaneoDao: EscaneoDao
) : ViewModel() {

    private val _datos = MutableStateFlow<List<DiaEstadistica>>(emptyList())
    val datos: StateFlow<List<DiaEstadistica>> = _datos

    init {
        cargarUltimos7Dias()
    }

    private fun cargarUltimos7Dias() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            // retroceder 6 días para tener 7 días en total
            cal.add(Calendar.DAY_OF_YEAR, -6)
            val inicioSemana = cal.timeInMillis

            val escaneos = escaneoDao.getEscaneosDesdeFecha(inicioSemana)

            val dias = mutableListOf<DiaEstadistica>()
            val etiquetas = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

            for (i in 0..6) {
                val diaInicio = inicioSemana + i * 86_400_000L
                val diaFin = diaInicio + 86_400_000L
                val diaEscaneos = escaneos.filter { it.fechaTimestamp in diaInicio until diaFin }
                val diaSemana = ((Calendar.getInstance().apply {
                    timeInMillis = diaInicio
                }.get(Calendar.DAY_OF_WEEK) + 5) % 7)

                dias.add(
                    DiaEstadistica(
                        etiqueta = etiquetas[diaSemana],
                        escaneos = diaEscaneos.size,
                        puntos   = diaEscaneos.sumOf { it.puntosGanados }
                    )
                )
            }
            _datos.value = dias
        }
    }
}
