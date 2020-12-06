package com.example.agentd.missionlist

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.databinding.FragmentMissionAgentBinding
import com.example.agentd.databinding.FragmentMissionListBinding
import com.example.agentd.databinding.FragmentMissionOrdererBinding
import com.example.agentd.missionagent.*
import com.example.agentd.missionorderer.MissionOrdererFragment
import com.example.agentd.missionorderer.MissionOrdererViewModel
import com.example.agentd.missionorderer.MissionOrdererViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MissionListFragment : Fragment() {

    private lateinit var missionListViewModel: MissionListViewModel
    private lateinit var missionList: MutableList<Mission>
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_list, container, false
        )

        val viewModelFactory = MissionListViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        missionListViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(MissionListViewModel::class.java)

        binding.missionListViewModel = missionListViewModel

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        val adapter = MissionAdapter(MissionListener { missionId ->
            // Toast.makeText(context, "${mission.missionId}", Toast.LENGTH_LONG).show()
            missionListViewModel.onNavigateToMissionDetail(missionId)
        })

        binding.missionListList.adapter = adapter

        binding.setLifecycleOwner(this)

        // set initial view as agent
//        missionListViewModel.onClickTopButton(1)


//        missionListViewModel.clickTopButton.observe(viewLifecycleOwner, Observer { index ->
//            if(index == 1) {
//                binding.listButtonAgent.setBackgroundColor(Color.RED)
//                binding.listButtonAgent.setBackgroundColor(Color.BLACK)
//            } else {
//                binding.listButtonAgent.setBackgroundColor(Color.BLACK)
//                binding.listButtonAgent.setBackgroundColor(Color.RED)
//            }
//            val ref = FirebaseDatabase.getInstance().getReference("/missions/")
//            ref.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(p0: DataSnapshot) {
//                    missionList = mutableListOf()
//
//                    p0.children.forEach cont@{
//                        val mission : Mission? = it.getValue(Mission::class.java)
//
//                        if(index == 1) { // index == 1 for agent
//                            if(mission!!.agentUid != uid) return@cont
//                        } else { // index == 2 for orderer
//                            if(mission!!.ordererUid != uid) return@cont
//                        }
//
//                        missionList.add(mission!!)
//                        Log.d(TAG, "Successfully read mission: $mission")
//                    }
//
//                    adapter.submitList(missionList)
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//                    Log.d(TAG, "Failed to load data from database: ", p0.toException())
//                }
//
//            })
//            missionListViewModel.doneClickTopButton()
//        })
//
//
//
//
//
//        missionListViewModel.navigateToMissionDetail.observe(viewLifecycleOwner, Observer { mission ->
//            mission?.let {
//                Log.d(TAG, "Try to show MissionDetailFragment")
//
//                if(uid == mission.agentUid) {
//                    this.findNavController().navigate(
//                        MissionListFragmentDirections.actionMissionListFragmentToMissionDetailFragment(
//                            missionId = it.missionId,
//                            fromTitle = false,
//                            fromOrderer = false,
//                            fromAgent = true
//                        )
//                    )
//                } else if (uid == mission.ordererUid) {
//                    this.findNavController().navigate(
//                        MissionListFragmentDirections.actionMissionListFragmentToMissionDetailFragment(
//                            missionId = it.missionId,
//                            fromTitle = false,
//                            fromOrderer = true,
//                            fromAgent = false
//                        )
//                    )
//                }
//                missionListViewModel.doneNavigateToMissionDetail()
//            }
//        })



        return binding.root
    }


    companion object {
        private const val TAG = "MissionList"
    }



}
