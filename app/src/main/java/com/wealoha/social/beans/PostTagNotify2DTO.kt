package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:29:46
 */
data class PostTagNotify2DTO(
    var postId: String,
    var fromUser: String
) : AbsNotify2DTO() {

}