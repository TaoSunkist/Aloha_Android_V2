package com.wealoha.social.beans

class MergeUsersGetData : ResultData() {
    @kotlin.jvm.JvmField
    var list: List<UserDTO>? = null
    @kotlin.jvm.JvmField
    var imageMap: Map<String, ImageCommonDto>? = null
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
}