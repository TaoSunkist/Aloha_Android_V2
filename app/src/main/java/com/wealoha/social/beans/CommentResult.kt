package com.wealoha.social.beans

data class CommentResult(
    var nextCursorId: String? = null,
    var userMap: Map<String, User>? = null,
    var list: List<Comment>? = null
) : ResultData() {

}