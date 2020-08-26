package com.wealoha.social.beans

data class MergeUsersGetData(
    var list: List<UserDTO>? = null,
    var imageMap: Map<String, ImageCommonDto>? = null,
    var nextCursorId: String? = null
) : ResultData() {

}