package com.wealoha.social.beans

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
class User : Parcelable, Serializable {
    @kotlin.jvm.JvmField
	var id: String? = null
    @kotlin.jvm.JvmField
	var type: String? = null
    @kotlin.jvm.JvmField
	var name: String? = null
    @kotlin.jvm.JvmField
	var birthday // yyyy-MM-dd
            : String? = null
    @kotlin.jvm.JvmField
	var age // TODO 改成int！！！
            : String? = null
    @kotlin.jvm.JvmField
	var height // 1+
            : String? = null
    @kotlin.jvm.JvmField
	var weight // 1+
            : String? = null

    /** 我当前所在的页面是否指向我自己  */
	@kotlin.jvm.JvmField
	var me = false

    /** 地区  */
	@kotlin.jvm.JvmField
	var regionCode: String? = null
    @kotlin.jvm.JvmField
	var region: List<String>? = null

    /** 星座  */
	@kotlin.jvm.JvmField
	var zodiac: String? = null

    /** 摘要简介  */
	@kotlin.jvm.JvmField
	var summary: String? = null

    /** 感兴趣的类型  */
	@kotlin.jvm.JvmField
	var selfPurposes: List<String>? = null

    /** 我自己的类型  */
	@kotlin.jvm.JvmField
	var selfTag: String? = null

    /** 头像  */
	@kotlin.jvm.JvmField
	var avatarImage: Image? = null
    @kotlin.jvm.JvmField
	var avatarImageId: String? = null

    // public int distance;
	@kotlin.jvm.JvmField
	var createTimeMillis: Long = 0

    /** 资料不完整 强制跳  */
	@kotlin.jvm.JvmField
	var profileIncomplete = false

    /** 他喜欢多少人  */
	@kotlin.jvm.JvmField
	var alohaCount = 0

    /** 他被多少人喜欢  */
	@kotlin.jvm.JvmField
	var alohaGetCount = 0

    /** 是否喜欢过他  */
	@kotlin.jvm.JvmField
	var aloha = false

    /** 匹配  */
	@kotlin.jvm.JvmField
	var match = false
    @kotlin.jvm.JvmField
	var postCount = 0

    @kotlin.jvm.JvmField
	@Deprecated("")
    var t: String? = null
    var phoneNum: String? = null
    @kotlin.jvm.JvmField
	var block = false
    @kotlin.jvm.JvmField
	var accessToken: String? = null
    var isUpdate = false

    /** 是否显示首次aloha time over后的提示  */
	@kotlin.jvm.JvmField
	var isShowAlohaTimeDialog = false

    /** 是否显示首次feed的引导  */
	@kotlin.jvm.JvmField
	var isShowFeedDialog = false
    @kotlin.jvm.JvmField
	var hasPrivacy = false

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 5164011748109511531L
        @kotlin.jvm.JvmField
		val TAG = User::class.java.simpleName
    }
}