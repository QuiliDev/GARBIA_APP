package com.garbia.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garbia.app.data.model.Logro
import com.garbia.app.data.repository.LogrosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    logrosRepository: LogrosRepository
) : ViewModel() {

    val logros: StateFlow<List<Logro>> = logrosRepository.getAllLogros()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
