package com.wealoha.social.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:57:11
 */
@Parcelize
data class PostLikeNotify2(
    override var type: Notify2Type,
    val unread: Boolean = false,
    var notifyid: String?,
    override var updateTimeMillis: Long,
    private val post: Post,
    private val user2s: List<User>,
    private var count: Int
) : AbsNotify2(type, unread, notifyid, updateTimeMillis), Parcelable {
    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun getPost(): Post {
        return post
    }

    fun getUsers(): List<User> {
        return user2s
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -1517780633471613601L
    }

}