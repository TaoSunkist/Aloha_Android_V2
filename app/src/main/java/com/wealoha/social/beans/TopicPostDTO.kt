package com.wealoha.social.beans

class TopicPostDTO {
    @kotlin.jvm.JvmField
    var postId: String? = null
    @kotlin.jvm.JvmField
    var type: FeedType? = null
    @kotlin.jvm.JvmField
    var description: String? = null
    @kotlin.jvm.JvmField
    var createTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var mine = false
    @kotlin.jvm.JvmField
    var venue: String? = null
    @kotlin.jvm.JvmField
    var venueId: String? = null
    @kotlin.jvm.JvmField
    var latitude: Double? = null
    @kotlin.jvm.JvmField
    var longitude: Double? = null
    @kotlin.jvm.JvmField
    var venueAbroad: Boolean? = null
    @kotlin.jvm.JvmField
    var user: UserDTO? = null
    @kotlin.jvm.JvmField
    var userTags: List<TopicPostTagDTO>? = null
    @kotlin.jvm.JvmField
    var image: ImageCommonDto? = null
    @kotlin.jvm.JvmField
    var video: VideoCommonDTO? = null
    @kotlin.jvm.JvmField
    var tagMe = false
    @kotlin.jvm.JvmField
    var liked = false
    @kotlin.jvm.JvmField
    var commentCount = 0
    @kotlin.jvm.JvmField
    var praiseCount = 0
    @kotlin.jvm.JvmField
    var recentComment: List<Comment2DTO>? = null
    @kotlin.jvm.JvmField
    var hashtag: HashTagDTO? = null
    @kotlin.jvm.JvmField
    var hasMoreComment = false
}