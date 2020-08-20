package com.wealoha.social.beans

import java.io.Serializable

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 下午12:23:34
 */
class NewAlohaNotify2(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,
    private var count: Int,
    private val user2s: List<User2>
) : AbsNotify2(type, unread, notifyid, updateTimeMillis), Serializable {
    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun getUser2s(): List<User2> {
        return user2s
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 5313343546495977449L
    }

}