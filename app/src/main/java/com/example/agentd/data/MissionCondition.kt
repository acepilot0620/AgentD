package com.example.agentd.data

import com.google.firebase.database.Exclude

data class MissionCondition(val content: String,
                            val complete: Boolean) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "content" to content,
            "complete" to complete,
        )
    }
}