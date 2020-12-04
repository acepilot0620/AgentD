package com.example.agentd.missionagent

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.agentd.R
import com.example.agentd.data.Mission
import com.example.agentd.databinding.FragmentMissionAgentBinding
import com.example.agentd.databinding.FragmentUserBinding
import com.example.agentd.title.TitleFragment
import com.example.agentd.title.TitleFragmentDirections
import com.example.agentd.user.UserFragment
import com.example.agentd.user.UserViewModel
import com.example.agentd.user.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MissionAgentFragment : Fragment() {

    private lateinit var missionAgentViewModel: MissionAgentViewModel
    private lateinit var missionList: MutableList<Mission>
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentMissionAgentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mission_agent, container, false
        )

        val viewModelFactory = MissionAgentViewModelFactory()

        // Get a reference to the ViewModel associated with this fragment.
        missionAgentViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(MissionAgentViewModel::class.java)

        binding.missionAgentViewModel = missionAgentViewModel

        uid = FirebaseAuth.getInstance().uid ?:""
        Log.d(TAG, uid)

        val adapter = MissionAgentAdapter(MissionListener { missionId ->
            // Toast.makeText(context, "${missionId}", Toast.LENGTH_LONG).show()
            missionAgentViewModel.onNavigateToMissionDetail(missionId)
        })

        binding.missionAgentList.adapter = adapter

        val ref = FirebaseDatabase.getInstance().getReference("/missions/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                missionList = mutableListOf()

                p0.children.forEach cont@{
                    val mission : Mission? = it.getValue(Mission::class.java)

                    if(mission!!.agentUid != uid) return@cont

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

        missionAgentViewModel.navigateToMissionDetail.observe(viewLifecycleOwner, Observer { missionId ->
                missionId?.let {
                    Log.d(TAG, "Try to show MissionDetailFragment")

                    this.findNavController().navigate(
                        MissionAgentFragmentDirections.actionMissionAgentFragmentToMissionDetailFragment(
                            missionId = it,
                            fromTitle = false,
                            fromOrderer = false,
                            fromAgent = true
                        )
                    )
                    missionAgentViewModel.doneNavigateToMissionDetail()
                }
            })


        return binding.root
    }




    companion object {
        private const val TAG = "MissionAgent"
    }




}
