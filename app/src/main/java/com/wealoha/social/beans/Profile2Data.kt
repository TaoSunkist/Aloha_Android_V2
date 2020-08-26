package com.wealoha.social.beans

data class Profile2Data(
    val user: UserDTO,
    val imageMap: Map<String, ImageCommonDto> = mapOf(),
    var friend: Boolean = false,
    var liked: Boolean = false
) : ResultData() {

}