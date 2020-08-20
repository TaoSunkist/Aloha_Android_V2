package com.wealoha.social.beans

class UserListGetData : ResultData() {
    @kotlin.jvm.JvmField
    var list: List<UserDTO>? = null
    @kotlin.jvm.JvmField
    var imageMap: Map<String, ImageCommonDto>? = null
    var nextCursorId: String? = null
    var alohaGetLocked = false
    var alohaGetUnlockCount = 0
}