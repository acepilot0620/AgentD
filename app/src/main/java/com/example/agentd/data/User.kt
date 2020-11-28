package com.example.agentd.data

data class User(val uid: String,
                val username: String,
                val email: String,
                val phoneNumber: String,
                val balance: Int,
                val latitude: Double,
                val longitude: Double)