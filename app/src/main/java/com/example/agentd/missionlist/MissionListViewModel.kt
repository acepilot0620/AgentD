package com.example.agentd.missionlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MissionListViewModel : ViewModel() {

    private val _navigateToMissionDetail = MutableLiveData<String?>()
    val navigateToMissionDetail: LiveData<String?>
        get() = _navigateToMissionDetail

    private val _clickOnDuty = MutableLiveData<Boolean?>()
    val clickOnDuty: LiveData<Boolean?>
        get() = _clickOnDuty

    private val _clickRequest = MutableLiveData<Boolean?>()
    val clickRequest: LiveData<Boolean?>
        get() = _clickRequest

    private val _clickTopButton = MutableLiveData<Int?>()
    val clickTopButton: LiveData<Int?>
        get() = _clickTopButton


    fun onNavigateToMissionDetail(missionId: String) {
        _navigateToMissionDetail.value = missionId
    }

    fun doneNavigateToMissionDetail() {
        _navigateToMissionDetail.value = null
    }

    fun onClickTopButton(index: Int) {
        _clickTopButton.value = index
    }

    fun doneClickTopButton() {
        _clickTopButton.value = null
    }

    fun onClickOnDuty() {
        _clickOnDuty.value = true
    }

    fun doneClickOnDuty() {
        _clickOnDuty.value = null
    }

    fun onClickRequest() {
        _clickRequest.value = true
    }

    fun doneClickRequest() {
        _clickRequest.value = null
    }

}