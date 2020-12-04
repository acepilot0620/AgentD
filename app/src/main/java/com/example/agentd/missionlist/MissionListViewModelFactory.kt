package com.example.agentd.missionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agentd.missionorderer.MissionOrdererViewModel

class MissionListViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionListViewModel::class.java)) {
            return MissionListViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}