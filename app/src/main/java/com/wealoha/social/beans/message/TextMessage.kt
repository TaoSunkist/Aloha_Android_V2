package com.wealoha.social.beans.message

import android.os.Parcelable
import com.mooveit.library.Fakeit
import kotlinx.android.parcel.Parcelize

/**
 * Created by walker on 14-4-27.
 */
@Parcelize
data class TextMessage(
    var text: String,
    override var id: String,
    var type: String = TYPE,
    var toUserId: String,
    override var state: String,
    override var createTimeMillis: Long = 0,
    /** 发送失败1、发送成功-1、正在发送0  */
    var smsStatus: Int = -1,
// 以下是本地状态
    /** 本地临时缓存的消息  */
    override var isLocal: Boolean = false,
    override var sending: Boolean = false,
    override var mine: Boolean,
    /** 是否发送成功  */
    override var sendFail: Boolean = false,
    /** 是否显示时间戳  */
    override var showTimestamp: Boolean = false
) : Message(
    id = id,
    isLocal = isLocal,
    sending = false,
    sendFail = sendFail,
    state = state,
    createTimeMillis = createTimeMillis,
    mine = mine,
    showTimestamp = showTimestamp
),
    Parcelable {

    override fun toString(): String {
        return text
    }

    companion object {
        private const val serialVersionUID = -567229340829512956L
        const val TYPE = "inboxMessageText"

        fun fake(toUserID: String, mine: Boolean = false): TextMessage {
            return TextMessage(
                id = System.currentTimeMillis().toString(),
                toUserId = toUserID,
                state = Integer.toHexString(listOf(0, 5, 10).random()),
                createTimeMillis = System.currentTimeMillis(),
                /** 发送失败1、发送成功-1、正在发送0  */
                smsStatus = listOf(1, 1, 1, 1, 1, -1, 0).random(),
                // 以下是本地状态
                mine = false,
                /** 本地临时缓存的消息  */
                isLocal = (0..1).random() == 1,
                sending = mine,
                /** 是否发送成功  */
                sendFail = true,
                /** 是否显示时间戳  */
                showTimestamp = (0..1).random() == 1,
                text = Fakeit.address().streetAddress() + Fakeit.address().zipCode()
            )
        }
    }

    init {
        type = TYPE
    }
}