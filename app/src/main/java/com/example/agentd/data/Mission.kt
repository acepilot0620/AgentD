package com.example.agentd.data

import com.google.firebase.database.Exclude

data class Mission(val missionId: String,
                   val ordererUid: String,
                   val agentUid: String,
                   val product: String,
                   val sourceLatitude: Double, val sourceLongitude: Double,
                   val destinationName: String, val destinationLatitude: Double, val destinationLongitude: Double,
                   val receiverPhone: String,
                   val reward: Int,
                   val condition1: String, val condition1Complete: Boolean,
                   val condition2: String, val condition2Complete: Boolean,
                   val condition3: String, val condition3Complete: Boolean,
                   val additionalInformation: String) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "missionId" to missionId,
            "ordererUid" to ordererUid,
            "agentUid" to agentUid,
            "product" to product,
            "sourceLatitude" to sourceLatitude,
            "sourceLongitude" to sourceLongitude,
            "destinationName" to destinationName, "destinationLatitude" to destinationLatitude, "destinationLongitude" to destinationLongitude,
            "receiverPhone" to receiverPhone, "reward" to reward,
            "condition1" to condition1, "condition1Complete" to condition1Complete,
            "condition2" to condition2, "condition2Complete" to condition2Complete,
            "condition3" to condition3, "condition3Complete" to condition3Complete,
            "additionalInformation" to additionalInformation
        )
    }

}