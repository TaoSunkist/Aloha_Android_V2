package com.wealoha.social.beans

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

}