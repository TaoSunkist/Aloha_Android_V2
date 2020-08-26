package com.wealoha.social.beans

data class UserListGetData(
    var list: List<UserDTO> = listOf(),
    var imageMap: Map<String, ImageCommonDto> = mapOf(),
    var nextCursorId: String? = null,
    var alohaGetLocked: Boolean = false,
    var alohaGetUnlockCount: Int = 0
) : ResultData() {

}