package com.wealoha.social.beans

data class FeedGetData(
    var list: List<PostDTO>? = null,
    var imageMap: Map<String, ImageCommonDto>? = null,
    var videoMap: Map<String, VideoCommonDTO>? = null,
    var commentCountMap: Map<String, Int>? = null,
    var likeCountMap: Map<String, Int>? = null,
    var userMap: Map<String, UserDTO>? = null,
    var nextCursorId: String? = null
) : ResultData() {

}