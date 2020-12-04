package com.example.agentd.missionlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MissionListViewModel : ViewModel() {

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