package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:29:03
 */
class PostCommentNotify2DTO : AbsNotify2DTO() {
    @kotlin.jvm.JvmField
    var replyMe = false
    @kotlin.jvm.JvmField
    var comment: String? = null
    @kotlin.jvm.JvmField
    var fromUser: String? = null
    @kotlin.jvm.JvmField
    var postId: String? = null
    @kotlin.jvm.JvmField
    var commentId: String? = null
}