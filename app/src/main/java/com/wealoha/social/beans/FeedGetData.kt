package com.wealoha.social.beans

class FeedGetData : ResultData() {
    @kotlin.jvm.JvmField
    var list: List<PostDTO>? = null
    @kotlin.jvm.JvmField
    var imageMap: Map<String, ImageCommonDto>? = null
    @kotlin.jvm.JvmField
    var videoMap: Map<String, VideoCommonDTO>? = null
    @kotlin.jvm.JvmField
    var commentCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var likeCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var userMap: Map<String, UserDTO>? = null
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
}