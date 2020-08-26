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


            val userDTOs = (0 until count).map {
                UserDTO.fake().apply {
                }
            }


            val postDTOs = arrayListOf<PostDTO>()
            val commentCountMap = hashMapOf<String, Int>()
            val imageMap = hashMapOf<String, ImageCommonDto>()
            val videoMap = hashMapOf<String, VideoCommonDTO>()
            val likeCountMap = hashMapOf<String, Int>()
            val userMap = hashMapOf<String, UserDTO>()

            userDTOs.map { userDTO ->
                val postDTO = PostDTO.fake(userDTO.id).apply {
                    imageId = userDTO.avatarImageId
                }
                postDTOs.add(postDTO)
                userMap[userDTO.id] = userDTO
                commentCountMap[postDTO.postId] = (0..100).random()
                likeCountMap[postDTO.postId] = (0..100).random()
                imageMap[userDTO.avatarImageId] =
                    ImageCommonDto.fake(imageID = userDTO.avatarImageId)
                videoMap[postDTO.videoId] = VideoCommonDTO.fake()
            }



            return FeedGetData(
                list = postDTOs,
                imageMap = imageMap,
                videoMap = videoMap,
                commentCountMap = commentCountMap,
                likeCountMap = likeCountMap,
                userMap = userMap,
                nextCursorId = Direct.Late.value
            )
        }
    }
}