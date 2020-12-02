package com.example.agentd.missionform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MissionFormViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionFormViewModel::class.java)) {
            return MissionFormViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}