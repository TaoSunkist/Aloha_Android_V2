package com.wealoha.social.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 下午12:23:34
 */
@Parcelize
data class NewAlohaNotify2(
    override var type: Notify2Type,
    val unread: Boolean,
    val notifyid: String?,
    override var updateTimeMillis: Long,
    private var count: Int,
    private val user2s: List<User>
) : AbsNotify2(type, unread, notifyid, updateTimeMillis), Serializable, Parcelable {
    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun getUsers(): List<User> {
        return user2s
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 5313343546495977449L
    }

}