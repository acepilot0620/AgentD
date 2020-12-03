package com.example.agentd.missionorderer

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.agentd.R

class MissionOrdererFragment : Fragment() {

    companion object {
        fun newInstance() = MissionOrdererFragment()
    }

    private lateinit var viewModel: MissionOrdererViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mission_orderer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MissionOrdererViewModel::class.java)
        // TODO: Use the ViewModel
    }

}