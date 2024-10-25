package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {
    private val _displayList = MutableStateFlow<List<String>>(listOf(""))
    private val _type = MutableStateFlow<String>("party")

    val displayList: StateFlow<List<String>> = _displayList
    val type: StateFlow<String> = _type

    init { getList() }

    private fun getList() = viewModelScope.launch {
        when (_type.value) {
            "party" -> dataRepo.getParties().collect { _displayList.emit(it) }
            "constituency" -> {
                dataRepo.getConstituencies().collect { constituencies ->
                    val filteredList = constituencies.map { if (it.isEmpty()) "Others" else it }
                    _displayList.emit(filteredList)
                }
            }
        }
    }

    fun setSortType(type: String) = viewModelScope.launch {
        _type.emit(type)
        getList()
    }
}