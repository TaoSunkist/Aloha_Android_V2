package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 下午12:25:04
 */
class PostTagNotify2(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,
    private val fromUser2: User2,
    private val post: Post
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getFromUser2(): User2 {
        return fromUser2
    }

    fun getPost(): Post {
        return post
    }

    companion object {
        private const val serialVersionUID = -346708852583028562L
    }

}