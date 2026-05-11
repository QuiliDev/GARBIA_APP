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
    val etiqueta: String,
    val escaneos: Int,
    val puntos: Int
)

data class TendenciaMaterial(
    val material: String,
    val colorHex: Long,
    val valoresPorDia: List<Float>
)

private val COLOR_MATERIAL = mapOf(
    "Plástico"      to 0xFF2196F3L,
    "Vidrio"        to 0xFF4CAF50L,
    "Papel"         to 0xFFFF9800L,
    "Cartón"        to 0xFFFF9800L,
    "Papel/Cartón"  to 0xFFFF9800L,
    "Metal"         to 0xFF78909CL,
    "Orgánico"      to 0xFF8BC34AL,
    "Biológico"     to 0xFF8BC34AL,
    "Ropa"          to 0xFF9C27B0L,
    "Calzado"       to 0xFF795548L,
    "Pilas"         to 0xFFE91E63L,
    "No reciclable" to 0xFFF44336L,
    "Basura"        to 0xFFF44336L,
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val escaneoDao: EscaneoDao
) : ViewModel() {

    private val _datos = MutableStateFlow<List<DiaEstadistica>>(emptyList())
    val datos: StateFlow<List<DiaEstadistica>> = _datos

    private val _tendencias = MutableStateFlow<List<TendenciaMaterial>>(emptyList())
    val tendencias: StateFlow<List<TendenciaMaterial>> = _tendencias

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
            cal.add(Calendar.DAY_OF_YEAR, -6)
            val inicioSemana = cal.timeInMillis

            val escaneos = escaneoDao.getEscaneosDesdeFecha(inicioSemana)

            val etiquetas = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            val rangos = (0..6).map { i ->
                val start = inicioSemana + i * 86_400_000L
                start to start + 86_400_000L
            }

            _datos.value = rangos.map { (start, end) ->
                val diaEscaneos = escaneos.filter { it.fechaTimestamp in start until end }
                val diaSemana = ((Calendar.getInstance().apply {
                    timeInMillis = start
                }.get(Calendar.DAY_OF_WEEK) + 5) % 7)
                DiaEstadistica(
                    etiqueta = etiquetas[diaSemana],
                    escaneos = diaEscaneos.size,
                    puntos   = diaEscaneos.sumOf { it.puntosGanados }
                )
            }

            _tendencias.value = escaneos.map { it.tipoMaterial }.distinct()
                .map { material ->
                    TendenciaMaterial(
                        material      = material,
                        colorHex      = COLOR_MATERIAL[material] ?: 0xFF9E9E9EL,
                        valoresPorDia = rangos.map { (start, end) ->
                            escaneos.count {
                                it.tipoMaterial == material && it.fechaTimestamp in start until end
                            }.toFloat()
                        }
                    )
                }
        }
    }
}
