package com.wealoha.social.beans

data class FeedGetData(
    var list: List<PostDTO> = arrayListOf(),
    var imageMap: Map<String, ImageCommonDto> = hashMapOf(),
    var videoMap: Map<String, VideoCommonDTO> = hashMapOf(),
    var commentCountMap: Map<String, Int> = hashMapOf(),
    var likeCountMap: Map<String, Int>? = hashMapOf(),
    var userMap: Map<String, UserDTO>? = hashMapOf(),
    var nextCursorId: String? = null
) : ResultData() {
    companion object {
//        fun fake(
//            cursor: String,
//            count: Int,
//            direct: Direct,
//            userid: String
//        ) {
//            return FeedGetData(
//
//            )
//        }
    }
}