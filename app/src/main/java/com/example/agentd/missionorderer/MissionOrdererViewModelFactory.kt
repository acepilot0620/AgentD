package com.example.agentd.missionorderer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MissionOrdererViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionOrdererViewModel::class.java)) {
            return MissionOrdererViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}