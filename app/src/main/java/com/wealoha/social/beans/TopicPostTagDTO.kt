package com.wealoha.social.beans

data class TopicPostTagDTO(
    val tagAnchorX: Double,
    val tagAnchorY: Double,
    val tagCenterX: Double,
    val tagCenterY: Double,
    val tagUser: UserDTO,
    val userId: String
) {

}