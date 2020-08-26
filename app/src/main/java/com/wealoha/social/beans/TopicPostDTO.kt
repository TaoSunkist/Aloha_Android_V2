package com.wealoha.social.beans

data class TopicPostDTO(

    var postId: String,

    var type: FeedType,

    var description: String = "",

    var createTimeMillis: Long = 0,

    var mine: Boolean = false,

    var venue: String? = null,

    var venueId: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    var venueAbroad: Boolean? = null,

    var user: UserDTO,

    var userTags: List<TopicPostTagDTO>? = null,

    val image: ImageCommonDto,

    val video: VideoCommonDTO,

    var tagMe: Boolean = false,

    var liked: Boolean = false,

    var commentCount: Int = 0,

    var praiseCount: Int = 0,

    var recentComment: List<Comment2DTO>? = null,

    var hashtag: HashTagDTO? = null,

    var hasMoreComment: Boolean = false
) {

}