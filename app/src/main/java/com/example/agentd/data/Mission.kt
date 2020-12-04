package com.example.agentd.data

import com.google.firebase.database.Exclude

data class Mission(val missionId: String = "",
                   val ordererUid: String = "",
                   val agentUid: String = "",
                   val product: String = "",
                   val sourceLatitude: Double = 0.0, val sourceLongitude: Double = 0.0,
                   val destinationName: String = "", val destinationLatitude: Double = 0.0, val destinationLongitude: Double = 0.0,
                   val receiverPhone: String = "",
                   val reward: Int = 0,
                   val condition1: String = "", val condition1Complete: Boolean = false,
                   val condition2: String = "", val condition2Complete: Boolean = false,
                   val condition3: String = "", val condition3Complete: Boolean = false,
                   val additionalInformation: String = "",
                   val status: String = "",
                   val completed: Boolean = false, val confirmed: Boolean = false) {

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
            "additionalInformation" to additionalInformation,
            "status" to status,
            "completed" to completed, "confirmed" to confirmed
        )
    }

}