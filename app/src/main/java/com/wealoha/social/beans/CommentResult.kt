package com.wealoha.social.beans

class CommentResult : ResultData() {
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
    @kotlin.jvm.JvmField
    var userMap: Map<String, User>? = null
    @kotlin.jvm.JvmField
    var list: List<Comment>? = null
}