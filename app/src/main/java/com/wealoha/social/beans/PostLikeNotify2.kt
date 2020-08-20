package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:57:11
 */
class PostLikeNotify2(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,
    private val post: Post,
    private val user2s: List<User2>,
    private var count: Int
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun getPost(): Post {
        return post
    }

    fun getUser2s(): List<User2> {
        return user2s
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -1517780633471613601L
    }

}