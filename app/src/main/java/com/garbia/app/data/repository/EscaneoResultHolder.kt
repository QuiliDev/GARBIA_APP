package com.garbia.app.data.repository

import com.garbia.app.data.model.ResultadoEscaneo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EscaneoResultHolder @Inject constructor() {
    var resultado: ResultadoEscaneo? = null
}
