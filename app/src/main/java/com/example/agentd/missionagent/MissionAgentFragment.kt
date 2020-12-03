package com.example.agentd.missionagent

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.agentd.R

class MissionAgentFragment : Fragment() {

    companion object {
        fun newInstance() = MissionAgentFragment()
    }

    private lateinit var viewModel: MissionAgentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mission_agent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MissionAgentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}