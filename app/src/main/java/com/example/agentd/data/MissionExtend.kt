package com.example.agentd.data

import com.google.firebase.database.Exclude

data class MissionExtend(val missionId: String,
                         val ordererUid: String,
                         val agentUid: String,
                         val product: String,
                         val sourceLatitude: Double, val sourceLongitude: Double,
                         val destinationName: String, val destinationLatitude: Double, val destinationLongitude: Double,
                         val receiverPhone: String,
                         val reward: Int,
                         val condition1: MissionCondition,
                         val condition2: MissionCondition,
                         val condition3: MissionCondition,
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
            "condition1" to condition1.toMap(),
            "condition2" to condition2.toMap(),
            "condition3" to condition3.toMap(),
            "additionalInformation" to additionalInformation
        )
    }

}