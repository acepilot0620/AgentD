package com.example.agentd.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val uid: String,
                val username: String,
                val email: String,
                val phoneNumber: String,
                val balance: Int,
                val latitude: Double,
                val longitude: Double) {

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