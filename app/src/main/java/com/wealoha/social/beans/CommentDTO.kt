package com.wealoha.social.beans

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
class CommentDTO {
    var createTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var userId: String? = null
    var mine = false
    var id: String? = null
    var type: String? = null
    var comment: String? = null

    /** 回复某人  */
    @kotlin.jvm.JvmField
    var replyUserId: String? = null
    var whisper = false

    /** 新版评论  */
    var user: UserDTO? = null
    var replyUser: UserDTO? = null
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as PostComment
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }
}