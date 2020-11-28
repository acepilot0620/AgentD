package com.example.agentd.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val uid: String = "",
                val username: String = "",
                val email: String = "",
                val phoneNumber: String = "",
                val balance: Long = 0,
                val latitude: Double = 0.0,
                val longitude: Double = 0.0) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "balance" to balance,
            "latitude" to latitude,
            "longitude" to longitude
        )
    }

}