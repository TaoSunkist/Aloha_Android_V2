package com.wealoha.social.beans.message

import com.wealoha.social.beans.User
import java.io.Serializable

/**
 * Created by walker on 14-4-14.
 */
class InboxSession : Serializable {
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
        val other = obj as InboxSession
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }

    @kotlin.jvm.JvmField
    var id: String? = null
    var type: String? = null
    @kotlin.jvm.JvmField
    var user: User? = null
    @kotlin.jvm.JvmField
    var updateTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var unread = 0
    @kotlin.jvm.JvmField
    var showMatchHint = false

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 3234191364565823058L
        val TAG = InboxSession::class.java.simpleName
    }
}