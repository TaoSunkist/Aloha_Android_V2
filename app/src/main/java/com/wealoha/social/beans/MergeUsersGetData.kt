package com.wealoha.social.beans

data class MergeUsersGetData(
    var list: List<UserDTO>,
    var imageMap: Map<String, ImageCommonDto>,
    var nextCursorId: String
) : ResultData() {

}