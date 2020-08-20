package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:27:25
 */
class PostLikeNotify2DTO : AbsNotify2DTO() {
    @kotlin.jvm.JvmField
    var userIds: List<String>? = null
    @kotlin.jvm.JvmField
    var postId: String? = null
}