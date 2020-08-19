package com.wealoha.social.beans

import android.os.Parcel
import android.os.Parcelable
import com.wealoha.social.beans.feed.UserTags
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * @author javamonk
 * @createTime 14-10-12 AM11:15
 */
@Parcelize
class Feed : Parcelable {
    @kotlin.jvm.JvmField
    var createTimeMillis: Long = 0

    @kotlin.jvm.JvmField
    var description: String? = null

    @kotlin.jvm.JvmField
    var imageId: String? = null
    var isMine = false

    @kotlin.jvm.JvmField
    var postId: String? = null

    /* VideoType */
    @kotlin.jvm.JvmField
    var type: String? = null

    @kotlin.jvm.JvmField
    var userId: String? = null
    var isLiked = false

    @kotlin.jvm.JvmField
    var latitude: Double? = null

    @kotlin.jvm.JvmField
    var longitude: Double? = null

    @kotlin.jvm.JvmField
    var venue: String? = null

    @kotlin.jvm.JvmField
    var userTags: List<UserTags>? = null

    @kotlin.jvm.JvmField
    var tagMe: Boolean? = null

    @kotlin.jvm.JvmField
    var venueAbroad: Boolean? = null
    var venueId: String? = null

    @kotlin.jvm.JvmField
    var commentCount = 0

    @kotlin.jvm.JvmField
    var likeCount = 0
    var videoId: String? = null
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (postId == null) 0 else postId.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Feed
        if (postId == null) {
            if (other.postId != null) return false
        } else if (postId != other.postId) return false
        return true
    }

    companion object {
        /**
         * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
         */
        @kotlin.jvm.JvmField
        val TAG = Feed::class.java.simpleName

    }
}