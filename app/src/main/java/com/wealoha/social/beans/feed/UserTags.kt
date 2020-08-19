package com.wealoha.social.beans.feed

import android.os.Parcel
import android.os.Parcelable
import com.wealoha.social.beans.User
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserTags : Parcelable {
    @kotlin.jvm.JvmField
	var tagAnchorX: Float? = null
    @kotlin.jvm.JvmField
	var tagAnchorY: Float? = null
    @kotlin.jvm.JvmField
	var tagCenterX: Float? = null
    @kotlin.jvm.JvmField
	var tagCenterY: Float? = null
    @kotlin.jvm.JvmField
	var tagUserId: String? = null
    @kotlin.jvm.JvmField
	var tagUser: User? = null

    // 以下属性是服务器没有的字段
    var username: String? = null
    @kotlin.jvm.JvmField
	var tagMe = false

    companion object {
        @kotlin.jvm.JvmField
		val TAG = UserTags::class.java.simpleName
    }
}