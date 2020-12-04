package com.example.agentd.missionagent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agentd.data.Mission
import com.example.agentd.databinding.ListItemMissionBinding

class MissionAdapter(val clickListener: MissionListener):
    ListAdapter<Mission, MissionAdapter.ViewHolder>(MissionDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!, clickListener)
    }


    class ViewHolder private constructor(val binding: ListItemMissionBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Mission, clickListener: MissionListener) {
            binding.mission = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMissionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}



class MissionDiffCallback : DiffUtil.ItemCallback<Mission>() {

    override fun areItemsTheSame(oldItem: Mission, newItem: Mission): Boolean {
        return oldItem.missionId == newItem.missionId
    }


    override fun areContentsTheSame(oldItem: Mission, newItem: Mission): Boolean {
        return oldItem == newItem
    }


}

class MissionListener(val clickListener: (missionId: String) -> Unit) {
    fun onClick(mission: Mission) = clickListener(mission.missionId)
}