package com.example.agentd.missionorderer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MissionOrdererViewModel : ViewModel() {

    private val _navigateToMissionDetail = MutableLiveData<String?>()
    val navigateToMissionDetail: LiveData<String?>
        get() = _navigateToMissionDetail

    private val _navigateToMissionAgent = MutableLiveData<Boolean?>()
    val navigateToMissionAgent: LiveData<Boolean?>
        get() = _navigateToMissionAgent


    fun onNavigateToMissionDetail(missionId: String) {
        _navigateToMissionDetail.value = missionId
    }

    fun doneNavigateToMissionDetail() {
        _navigateToMissionDetail.value = null
    }


    fun onNavigateToMissionAgent() {
        _navigateToMissionAgent.value = true
    }

    fun doneNavigateToMissionAgent() {
        _navigateToMissionAgent.value = null
    }

}