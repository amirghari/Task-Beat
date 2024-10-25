package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.ParliamentMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MemberListViewModel(
    savedStateHandle: SavedStateHandle,
    private val dataRepo: DataRepository,
) : ViewModel() {
    private var type: String? = savedStateHandle.get<String>("type")
    private var selectedType: String? = savedStateHandle.get<String>("selected")

    private val _pmList = MutableStateFlow<List<Pair<ParliamentMember, Boolean>>>(listOf())
    val pmList: StateFlow<List<Pair<ParliamentMember, Boolean>>> = _pmList

    init { getPMList() }

    fun getPMList() {
        when (type) {
            "party" -> viewModelScope.launch {
                dataRepo.getAllPMWithParty(selectedType!!).collect { pmList ->
                    val pmWithFavorites = pmList.map { member ->
                        val isFavorite =
                            dataRepo.getFavoriteById(member.hetekaId).firstOrNull() ?: false
                        Pair(member, isFavorite)
                    }
                    _pmList.emit(pmWithFavorites)
                }
            }

            "constituency" -> viewModelScope.launch {
                if (selectedType == "Others") selectedType = ""
                dataRepo.getAllPMWithConstituency(selectedType!!).collect { pmList ->
                    val pmWithFavorites = pmList.map { member ->
                        val isFavorite = dataRepo.getFavoriteById(member.hetekaId).firstOrNull() ?: false
                        Pair(member, isFavorite)
                    }
                    _pmList.emit(pmWithFavorites)
                }
            }
        }
    }

    fun changeFavorite(id: Int) = viewModelScope.launch {
        dataRepo.toggleFavorite(id)
        getPMList()
    }
}