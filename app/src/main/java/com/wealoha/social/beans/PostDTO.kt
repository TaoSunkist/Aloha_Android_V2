package com.wealoha.social.beans

import com.mooveit.library.Fakeit

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:33:11
 */
data class PostDTO(

    var createTimeMillis: Long = 0,

    var description: String,

    var imageId: String,

    var videoId: String,

    var postId: String,

    var mine: Boolean = false,

    var liked: Boolean = false,

    var type: String,

    var userId: String,

    var latitude: Double? = null,

    var longitude: Double? = null,

    var venue: String? = null,

    var userTags: List<UserTagsDTO> = listOf(),

    var venueAbroad: Boolean? = null,

    var venueId: String? = null,

    var count: Int = 0,

    var recentComments: List<Comment2DTO> = listOf(),

    var hashtag: HashTagDTO? = null,

    var hasMoreComment: Boolean = false
) {
    companion object {

        fun fake(userID: String): PostDTO {

//        VideoPost("VideoPost", 0),  //
//        ImagePost("ImagePost", 1);
            return PostDTO(
                createTimeMillis = System.currentTimeMillis(),
                description = Fakeit.book().genre(),
                imageId = System.currentTimeMillis().toString(),
                videoId = System.currentTimeMillis().toString(),
                postId = System.currentTimeMillis().toString(),
                mine = (0..1).random() == 1,
                liked = (0..1).random() == 1,
                type = FeedType.values().random().value,
                userId = userID,
                latitude = (100..180).random().toDouble(),
                longitude = (100..180).random().toDouble(),
                venue = Fakeit.book().author(),
                userTags = (0..5).map { UserTagsDTO.fake(userID = userID) },
                venueAbroad = (0..1).random() == 1,
                count = (0..20).random(),
                recentComments = (0..10).map { Comment2DTO.fake() },
                hashtag = HashTagDTO.fake(),
                hasMoreComment = (0..1).random() == 1
            )
        }
    }
}