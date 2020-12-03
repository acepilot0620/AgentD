package com.example.agentd.missiondetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.agentd.R
import com.example.agentd.databinding.FragmentMissionDetailBinding
import com.example.agentd.databinding.FragmentTitleBinding
import com.example.agentd.title.TitleViewModel
import com.example.agentd.title.TitleViewModelFactory

class MissionDetailFragment : Fragment() {

    companion object {
        fun newInstance() = MissionDetailFragment()
    }

    private lateinit var viewModel: MissionDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_detail, container, false)

        val viewModelFactory = MissionDetailViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        val missionDetailViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MissionDetailViewModel::class.java)

        binding.missionDetailViewModel = missionDetailViewModel

        binding.setLifecycleOwner(this)

        return binding.root
    }

}