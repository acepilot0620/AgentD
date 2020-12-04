package com.example.agentd.missionorderer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agentd.data.Mission
import com.example.agentd.databinding.ListItemMissionBinding
import com.example.agentd.missionagent.MissionDiffCallback
import com.example.agentd.missionagent.MissionListener

class MissionOrdererAdapter(val clickListener: MissionListener):
    ListAdapter<Mission, MissionOrdererAdapter.ViewHolder>(MissionDiffCallback()) {


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