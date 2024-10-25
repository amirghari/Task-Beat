package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.ParliamentMember
import com.example.taskbeat.model.ParliamentMemberExtra
import com.example.taskbeat.model.ParliamentMemberLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MemberViewModel(
    savedStateHandle: SavedStateHandle,
    private val dataRepo: DataRepository,
) : ViewModel() {
    private var hetekaId: String? = savedStateHandle.get<String>("param")
    private val _member = MutableStateFlow<ParliamentMember>(
        ParliamentMember(
            hetekaId = 0,
            seatNumber = 0,
            lastname = "lastname",
            firstname = "firstname",
            party = "party",
            minister = false,
            pictureUrl = ""
        )
    )
    private val _memberExtra = MutableStateFlow<ParliamentMemberExtra>(
        ParliamentMemberExtra(
            hetekaId = 0,
            twitter = null,
            bornYear = 0,
            constituency = ""
        )
    )
    private val _memberLocal = MutableStateFlow<ParliamentMemberLocal>(
        ParliamentMemberLocal(
            hetekaId = 0,
            favorite = false,
            note = null
        )
    )

    val member: StateFlow<ParliamentMember> = _member
    val memberExtra: StateFlow<ParliamentMemberExtra> = _memberExtra
    val memberLocal: StateFlow<ParliamentMemberLocal> = _memberLocal

    init { getData() }

    fun getData() = viewModelScope.launch {
        fetchMember()
        fetchMemberExtra()
        fetchMemberLocal()
    }

    private suspend fun fetchMember() {
        val member = dataRepo.getMemberWithId(hetekaId!!.toInt()).first()
        _member.emit(member)
    }

    private suspend fun fetchMemberExtra() {
        val memberExtra = dataRepo.getMemberExtraWithId(hetekaId!!.toInt()).first()
        _memberExtra.emit(memberExtra)
    }

    private suspend fun fetchMemberLocal() {
        val memberLocal = dataRepo.getMemberLocalWithId(hetekaId!!.toInt()).first()
        if (memberLocal != null) {
            _memberLocal.emit(memberLocal)
        }
    }

    fun changeFavorite(id: Int) = viewModelScope.launch {
        dataRepo.toggleFavorite(id)
        fetchMemberLocal()
    }
}