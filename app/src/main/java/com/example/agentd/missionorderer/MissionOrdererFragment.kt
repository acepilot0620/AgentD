package com.example.agentd.missionorderer

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
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.databinding.FragmentMissionOrdererBinding
import com.example.agentd.missionagent.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MissionOrdererFragment : Fragment() {


    private lateinit var missionOrdererViewModel: MissionOrdererViewModel
    private lateinit var missionList: MutableList<Mission>
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionOrdererBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_orderer, container, false
        )

        val viewModelFactory = MissionOrdererViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        missionOrdererViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(MissionOrdererViewModel::class.java)

        binding.missionOrdererViewModel = missionOrdererViewModel

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        val adapter = MissionAdapter(MissionListener { missionId ->
            // Toast.makeText(context, "${missionId}", Toast.LENGTH_LONG).show()
            missionOrdererViewModel.onNavigateToMissionDetail(missionId)
        })

        binding.missionOrdererList.adapter = adapter


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

        missionOrdererViewModel.navigateToMissionDetail.observe(viewLifecycleOwner, Observer { missionId ->
            missionId?.let {
                Log.d(TAG, "Try to show MissionDetailFragment")

                this.findNavController().navigate(
                    MissionOrdererFragmentDirections.actionMissionOrdererFragmentToMissionDetailFragment(
                        missionId = it,
                        fromTitle = false,
                        fromOrderer = true,
                        fromAgent = false
                    )
                )
                missionOrdererViewModel.doneNavigateToMissionDetail()
            }
        })


        missionOrdererViewModel.navigateToMissionAgent.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                Log.d(TAG, "Try to show MissionAgentFragment")

                this.findNavController().navigate(
                    MissionOrdererFragmentDirections
                        .actionMissionOrdererFragmentToMissionAgentFragment()
                )
                missionOrdererViewModel.doneNavigateToMissionAgent()
            }
        })



        return binding.root
    }


    companion object {
        private const val TAG = "MissionOrderer"
    }


}