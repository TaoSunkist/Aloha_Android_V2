package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 下午12:25:04
 */
class PostCommentNotify2(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,
    private val replyMe: Boolean,
    private val comment: String,
    private val commentId: String,
    private val fromUser2: User2,
    private val post: Post,
    private var count: Int
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun isReplyMe(): Boolean {
        return replyMe
    }

    fun getCommentId(): String {
        return commentId
    }

    fun getComment(): String {
        return comment
    }

    fun getFromUser2(): User2 {
        return fromUser2
    }

    fun getPost(): Post {
        return post
    }

    companion object {
        private const val serialVersionUID = 6159518661888749040L
    }

}