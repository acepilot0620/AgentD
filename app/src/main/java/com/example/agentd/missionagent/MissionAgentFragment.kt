package com.example.agentd.missionagent

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.databinding.FragmentMissionAgentBinding
import com.example.agentd.databinding.FragmentUserBinding
import com.example.agentd.user.UserFragment
import com.example.agentd.user.UserViewModel
import com.example.agentd.user.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MissionAgentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    // private lateinit var adapter: RecyclerView.Adapter

    private lateinit var missionAgentViewModel: MissionAgentViewModel
    private lateinit var missionList: List<Mission>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionAgentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_agent, container, false)

        val viewModelFactory = MissionAgentViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        missionAgentViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MissionAgentViewModel::class.java)

        binding.missionAgentViewModel = missionAgentViewModel


        recyclerView = binding.missionList


        binding.setLifecycleOwner(this)










        return binding.root
    }




}
