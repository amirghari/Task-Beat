package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.ParliamentMemberLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val dataRepo: DataRepository
) : ViewModel() {
    private var hetekaId: String? = savedStateHandle.get<String>("param")
    private val _memberLocal = MutableStateFlow<ParliamentMemberLocal>(
        ParliamentMemberLocal(
            hetekaId = 0,
            favorite = false,
            note = null
        )
    )
    val memberLocal: StateFlow<ParliamentMemberLocal> = _memberLocal

    init { getData() }

    fun getData() = viewModelScope.launch {
        dataRepo.getMemberLocalWithId(hetekaId!!.toInt()).collect {
            if (it != null) _memberLocal.emit(it)
        }
    }
    fun saveNote(id: Int, note: String) = viewModelScope.launch { dataRepo.updateNoteWithId(id, note) }
    fun deleteNote(id: Int) = viewModelScope.launch { dataRepo.deleteNoteWithId(id) }
}