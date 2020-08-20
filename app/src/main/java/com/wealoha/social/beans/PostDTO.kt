package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:33:11
 */
class PostDTO {
    @kotlin.jvm.JvmField
    var createTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var description: String? = null
    @kotlin.jvm.JvmField
    var imageId: String? = null
    @kotlin.jvm.JvmField
    var videoId: String? = null
    @kotlin.jvm.JvmField
    var postId: String? = null
    @kotlin.jvm.JvmField
    var mine = false
    @kotlin.jvm.JvmField
    var liked = false
    @kotlin.jvm.JvmField
    var type: String? = null
    @kotlin.jvm.JvmField
    var userId: String? = null
    @kotlin.jvm.JvmField
    var latitude: Double? = null
    @kotlin.jvm.JvmField
    var longitude: Double? = null
    @kotlin.jvm.JvmField
    var venue: String? = null
    @kotlin.jvm.JvmField
    var userTags: List<UserTagsDTO>? = null
    @kotlin.jvm.JvmField
    var venueAbroad: Boolean? = null
    @kotlin.jvm.JvmField
    var venueId: String? = null
    var count = 0
    @kotlin.jvm.JvmField
    var recentComments: List<Comment2DTO>? = null
    @kotlin.jvm.JvmField
    var hashtag: HashTagDTO? = null
    @kotlin.jvm.JvmField
    var hasMoreComment = false
}