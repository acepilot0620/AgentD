package com.example.agentd.missiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agentd.data.Mission

class MissionDetailViewModel() : ViewModel() {

    private val _acceptMission = MutableLiveData<Boolean?>()
    val acceptMission: LiveData<Boolean?>
        get() = _acceptMission

    private val _navigateToTitle = MutableLiveData<Boolean?>()
    val navigateToTitle: LiveData<Boolean?>
        get() = _navigateToTitle

    private val _completeMission = MutableLiveData<Boolean?>()
    val completeMission: LiveData<Boolean?>
        get() = _completeMission

    private val _confirmMissionResult = MutableLiveData<Boolean?>()
    val confirmMissionResult: LiveData<Boolean?>
        get() = _confirmMissionResult


    private val _checkBoxChecked = MutableLiveData<Int?>()
    val checkBoxChecked: LiveData<Int?>
        get() = _checkBoxChecked


    fun onAcceptMission() {
        _acceptMission.value = true
    }

    fun doneAcceptMission() {
        _acceptMission.value = false
    }

    fun onNavigateToTitle() {
        _navigateToTitle.value = true
    }

    fun doneNavigateToTitle() {
        _navigateToTitle.value = false
    }

    fun onCompleteMission() {
        _completeMission.value = true
    }

    fun doneCompleteMission() {
        _completeMission.value = false
    }

    fun onConfirmMissionResult() {
        _confirmMissionResult.value = true
    }

    fun doneConfirmMissionResult() {
        _confirmMissionResult.value = false
    }

    fun onCheckBoxChecked(index: Int) {
        _checkBoxChecked.value = index
    }

    fun doneCheckBoxChecked() {
        _checkBoxChecked.value = null
    }






}