package com.example.agentd.missionagent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.data.Mission

class MissionAgentViewModel : ViewModel() {

    private val _navigateToMissionDetail = MutableLiveData<String?>()
    val navigateToMissionDetail: LiveData<String?>
        get() = _navigateToMissionDetail

    private val _navigateToMissionOrderer = MutableLiveData<Boolean?>()
    val navigateToMissionOrderer: LiveData<Boolean?>
        get() = _navigateToMissionOrderer


    fun onNavigateToMissionDetail(missionId: String) {
        _navigateToMissionDetail.value = missionId
    }

    fun doneNavigateToMissionDetail() {
        _navigateToMissionDetail.value = null
    }

    fun onNavigateToMissionOrderer() {
        _navigateToMissionOrderer.value = true
    }

    fun doneNavigateToMissionOrderer() {
        _navigateToMissionOrderer.value = null
    }

}