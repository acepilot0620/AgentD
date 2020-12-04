package com.example.agentd.missionlist

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
            // Toast.makeText(context, "${missionId}", Toast.LENGTH_LONG).show()
            missionListViewModel.onNavigateToMissionDetail(missionId)
        })

        binding.missionListList.adapter = adapter


        val ref = FirebaseDatabase.getInstance().getReference("/missions/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                missionList = mutableListOf()

                p0.children.forEach cont@{
                    val mission : Mission? = it.getValue(Mission::class.java)

                    if(mission!!.ordererUid != uid) return@cont

                    missionList.add(mission!!)
                    Log.d(TAG, "Successfully read mission: $mission")
                }

                adapter.submitList(missionList)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Failed to load data from database: ", p0.toException())
            }

        })


        binding.setLifecycleOwner(this)


        missionListViewModel.navigateToMissionDetail.observe(viewLifecycleOwner, Observer { missionId ->
            missionId?.let {
                Log.d(TAG, "Try to show MissionDetailFragment")

//                this.findNavController().navigate(
//                    MissionListFragmentDirections.actionMissionListFragmentToMissionDetailFragment(
//                        missionId = it,
//                        fromTitle = false,
//                        fromOrderer = true,
//                        fromAgent = false
//                    )
//                )
                missionListViewModel.doneNavigateToMissionDetail()
            }
        })



        return binding.root
    }


    companion object {
        private const val TAG = "MissionList"
    }



}
