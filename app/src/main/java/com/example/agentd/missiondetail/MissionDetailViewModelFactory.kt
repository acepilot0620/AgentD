package com.example.agentd.missiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agentd.data.Mission

class MissionDetailViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionDetailViewModel::class.java)) {
            return MissionDetailViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}