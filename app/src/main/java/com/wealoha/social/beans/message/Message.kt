package com.wealoha.social.beans.message

import java.io.Serializable

/**
 * Created by walker on 14-4-15.
 */
abstract class Message : Serializable {
    @kotlin.jvm.JvmField
    var id: String? = null
    var type: String? = null
    var toUserId: String? = null
    @kotlin.jvm.JvmField
    var mine = false
    @kotlin.jvm.JvmField
    var state: String? = null
    @kotlin.jvm.JvmField
    var createTimeMillis: Long = 0

    /** 发送失败1、发送成功-1、正在发送0  */
    @kotlin.jvm.JvmField
    var smsStatus = -1
    // 以下是本地状态
    /** 本地临时缓存的消息  */
    @kotlin.jvm.JvmField
    var isLocal = false
    @kotlin.jvm.JvmField
    var sending = false
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id.hashCode()
        return result
    }

    /** 是否发送成功  */
    @kotlin.jvm.JvmField
    var sendFail = false

    /** 是否显示时间戳  */
    @kotlin.jvm.JvmField
    var showTimestamp = false
    override fun toString(): String {
        return "Message [id=$id, type=$type, toUserId=$toUserId, mine=$mine, state=$state, createTimeMillis=$createTimeMillis, smsStatus=$smsStatus, isLocal=$isLocal, sending=$sending, sendFail=$sendFail, showTimestamp=$showTimestamp]"
    }

    companion object {
        private const val serialVersionUID = -89575831597711736L
    }
}