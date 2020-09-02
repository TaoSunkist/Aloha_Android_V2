package com.wealoha.social.beans.message

import android.os.Parcelable
import com.mooveit.library.Fakeit
import com.wealoha.social.beans.User
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.lang.IllegalArgumentException

/**
 * Created by walker on 14-4-14.
 */
@Parcelize
data class InboxSession(
    var id: String,
    var user: User,
    val type: String,
    var updateTimeMillis: Long = 0,
    var unread: Int = 0,
    var showMatchHint: Boolean = false
) : Serializable, Parcelable {
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

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 3234191364565823058L
        val TAG = InboxSession::class.java.simpleName

        fun fake(): InboxSession {
            return InboxSession(
                id = System.currentTimeMillis().toString(),
                user = User.fake(me = false, isAuthentication = true),
                updateTimeMillis = System.currentTimeMillis(),
                unread = (0..1000).random(),
                type = when ((0..1).random()) {
                    0 -> {
                        TextMessage.TYPE
                    }
                    1 -> {
                        ImageMessage.TYPE
                    }
                    else -> throw IllegalArgumentException("")
                },
                showMatchHint = (0..1).random() == 1
            )
        }

        fun fakeForList(): List<InboxSession> {
            return (0..10).map {
                fake()
            }.toList()
        }
    }
}