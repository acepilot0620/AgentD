package com.example.agentd.missionagent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agentd.missiondetail.MissionDetailViewModel

class MissionAgentViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionAgentViewModel::class.java)) {
            return MissionAgentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}