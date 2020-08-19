package com.wealoha.social.beans.common

import com.wealoha.social.beans.ResultData

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午5:57:38
 */
class ConstantsData : ResultData() {
    @kotlin.jvm.JvmField
    var hasUpdateVersion = false
    var officialUserId: String? = null
    @kotlin.jvm.JvmField
    var updateDetails: String? = null
    @kotlin.jvm.JvmField
    var startupImageShowIntervalMinutes = 0

    /**
     * 开机画面
     */
    @kotlin.jvm.JvmField
    var startupImageMap: Map<String, String>? = null

    companion object {
        const val STARTUP_IMAGE_720P = "Android720p"
        const val STARTUP_IMAGE_1080P = "Android1080p"
    }
}