package com.wealoha.social.beans

import android.text.TextUtils
import java.io.Serializable

class Location : Serializable {
    @kotlin.jvm.JvmField
    var address: String? = null
    @kotlin.jvm.JvmField
    var name: String? = null
    @kotlin.jvm.JvmField
    var id: String? = null
    @kotlin.jvm.JvmField
    var latitude: Double? = null
    @kotlin.jvm.JvmField
    var longitude: Double? = null

    /**
     * @Title: isEmpty
     * @Description: 地理位置信息是否为空
     */
    val isEmpty: Boolean
        get() = if (TextUtils.isEmpty(id) || latitude == null || longitude == null) {
            true
        } else false

    companion object {
        /**
         * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
         */
        private const val serialVersionUID = -3591352045359882813L
    }
}