package com.wealoha.social.beans

data class UserTagsDTO(

    var tagAnchorX: Double,

    var tagAnchorY: Double,


    var tagCenterX: Double,


    var tagCenterY: Double,


    var tagUserId: String
) {

    companion object {
        fun fake(userID: String): UserTagsDTO {
            return UserTagsDTO(
                tagAnchorX = (50..100).random().toDouble(),
                tagAnchorY = (50..100).random().toDouble(),
                tagCenterX = (50..100).random().toDouble(),
                tagCenterY = (50..100).random().toDouble(),
                tagUserId = userID
            )
        }
    }

}