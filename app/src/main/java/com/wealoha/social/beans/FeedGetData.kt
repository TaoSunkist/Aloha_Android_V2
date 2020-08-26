package com.wealoha.social.beans

data class FeedGetData(
    var list: List<PostDTO> = arrayListOf(),
    var imageMap: Map<String, ImageCommonDto> = hashMapOf(),
    var videoMap: Map<String, VideoCommonDTO> = hashMapOf(),
    var commentCountMap: Map<String, Int> = hashMapOf(),
    var likeCountMap: Map<String, Int> = hashMapOf(),
    var userMap: Map<String, UserDTO> = hashMapOf(),
    var nextCursorId: String
) : ResultData() {
    companion object {
        fun fake(
            cursor: String,
            count: Int,
            direct: Direct,
            userid: String
        ): FeedGetData {
            val postDTOs = (0..count).map {
                PostDTO.fake()
            }

            val commentCountMap = hashMapOf<String, Int>().apply {
                (0..20).map {
                    put(it.toString(), (0..20).random())
                }
            }
            val likeCountMap = hashMapOf<String, Int>().apply {
                (0..20).map {
                    put(it.toString(), (0..20).random())
                }
            }

            return FeedGetData(
                list = postDTOs,
                imageMap = ImageCommonDto.fakeForMap(),
                videoMap = VideoCommonDTO.fakeForMap(),
                commentCountMap = commentCountMap,
                likeCountMap = likeCountMap,
                userMap = UserDTO.fakeForMap(),
                nextCursorId = Direct.Late.value
            )
        }
    }
}