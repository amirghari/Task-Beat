package com.example.rhythmplus.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhythmplus.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopBarViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {
    private var _isDarkTheme = MutableStateFlow<Boolean>(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    init {
        viewModelScope.launch {
            dataRepo.isDarkThemeFlow.collect { _isDarkTheme.emit(it) }
        }
    }

    fun toggleTheme() = viewModelScope.launch { dataRepo.toggleTheme() }
}