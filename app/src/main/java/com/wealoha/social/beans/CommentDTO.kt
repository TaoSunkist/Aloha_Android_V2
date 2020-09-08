package com.wealoha.social.beans

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
data class CommentDTO(
    var createTimeMillis: Long = 0,
    var userId: String,
    var mine: Boolean = false,
    var id: String,
    var type: String? = null,
    var comment: String,

    /** 回复某人  */
    var replyUserId: String,
    var whisper: Boolean = false,

    /** 新版评论  */
    var user: UserDTO,
    var replyUser: UserDTO
) {

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

    companion object {
        fun fakeForList(toUserDTO: UserDTO, fromUserDTO: UserDTO): CommentDTO {
            return CommentDTO(
                createTimeMillis = 0,
                userId = "",
                mine = false,
                id = "",
                type = null,
                comment = "",
                replyUserId = "",
                whisper = false,
                user = fromUserDTO,
                replyUser = toUserDTO
            )
        }
    }
}