package com.wealoha.social.beans.setting

import com.wealoha.social.beans.ResultData

/**
 * @author javamonk
 * @createTime 14-10-11 PM5:53
 */
class PushSettingResult : ResultData() {
    @kotlin.jvm.JvmField
    var pushEnable = false
    @kotlin.jvm.JvmField
    var pushSound = false
    @kotlin.jvm.JvmField
    var pushVibration = false
    @kotlin.jvm.JvmField
    var pushShowDetail = false
    @kotlin.jvm.JvmField
    var pushPostLike: String? = null
    @kotlin.jvm.JvmField
    var pushPostComment: String? = null
    @kotlin.jvm.JvmField
    var pushAloha: String? = null
    @kotlin.jvm.JvmField
    var pushPostTag: String? = null
}