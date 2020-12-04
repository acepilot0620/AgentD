package com.example.agentd.missionagent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.data.Mission

class MissionAgentViewModel : ViewModel() {

    private val _navigateToMissionDetail = MutableLiveData<String?>()
    val navigateToMissionDetail: LiveData<String?>
        get() = _navigateToMissionDetail


    fun onNavigateToMissionDetail(missionId: String) {
        _navigateToMissionDetail.value = missionId
    }

    fun doneNavigateToMissionDetail() {
        _navigateToMissionDetail.value = null
    }

}