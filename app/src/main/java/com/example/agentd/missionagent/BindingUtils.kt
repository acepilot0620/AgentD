package com.example.agentd.missionagent

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.agentd.data.Mission


@BindingAdapter("setProductText")
fun TextView.setProductText(item: Mission?) {
    item?.let {
        text = item.product
    }
}

@BindingAdapter("setDestinationNameText")
fun TextView.setDestinationNameText(item: Mission?) {
    item?.let {
        text = item.destinationName
    }
}