package com.example.agentd.missionform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MissionFormViewModel : ViewModel() {

    private val _sendMissionInformation = MutableLiveData<Boolean?>()
    val sendMissionInformation: LiveData<Boolean?>
        get() = _sendMissionInformation

    private val _navigateToTitle = MutableLiveData<Boolean?>()
    val navigateToTitle: LiveData<Boolean?>
        get() = _navigateToTitle

    fun onCreateMission() {
        _sendMissionInformation.value = true
    }

    fun doneCreateMission() {
        _sendMissionInformation.value = null
    }

    fun onNavigateToTitle() {
        _navigateToTitle.value = true
    }

    fun doneNavigateToTitle() {
        _navigateToTitle.value = null
    }

}