package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:29:03
 */
data class PostCommentNotify2DTO(

    var replyMe: Boolean = false,

    var comment: String,

    var fromUser: String,

    var postId: String,

    var commentId: String
) : AbsNotify2DTO() {

}