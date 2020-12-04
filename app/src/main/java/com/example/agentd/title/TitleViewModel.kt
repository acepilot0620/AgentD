package com.example.agentd.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.data.Mission

class TitleViewModel : ViewModel() {

    private val _navigateToUser = MutableLiveData<Boolean?>()
    val navigateToUser: LiveData<Boolean?>
        get() = _navigateToUser

    private val _navigateToMissionForm = MutableLiveData<Boolean?>()
    val navigateToMissionForm: LiveData<Boolean?>
        get() = _navigateToMissionForm

    private val _navigateToMissionDetail = MutableLiveData<Mission?>()
    val navigateToMissionDetail: LiveData<Mission?>
        get() = _navigateToMissionDetail


    fun onNavtigateToUser() {
        _navigateToUser.value = true
    }

    fun doneNavigateToUser() {
        _navigateToUser.value = null
    }

    fun onNavigateToMissionForm() {
        _navigateToMissionForm.value = true
    }

    fun doneNavigateToMissionForm() {
        _navigateToMissionForm.value = null
    }

    fun onNavigateToMissionDetail(mission: Mission) {
        _navigateToMissionDetail.value = mission
    }

    fun doneNavigateToMissionDetail() {
        _navigateToMissionDetail.value = null
    }

}