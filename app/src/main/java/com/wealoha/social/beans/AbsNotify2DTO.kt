package com.wealoha.social.beans

/**
 * Notify2的数据传输对象，和API保持一致
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:24:44
 */
open class AbsNotify2DTO {
    @kotlin.jvm.JvmField
    var notifyId: String? = null
    @kotlin.jvm.JvmField
    var type: String? = null
    @kotlin.jvm.JvmField
    var unread = false
    @kotlin.jvm.JvmField
    var updateTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var count = 0
}