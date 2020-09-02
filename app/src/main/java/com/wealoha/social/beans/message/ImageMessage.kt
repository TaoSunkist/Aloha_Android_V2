package com.wealoha.social.beans.message

import android.os.Parcelable
import com.wealoha.social.beans.Image
import kotlinx.android.parcel.Parcelize

/**
 * Created by walker on 14-4-27.
 */
@Parcelize
data class ImageMessage(
    override var id: String,
    var type: String = TYPE,
    var toUserId: String,
    override var mine: Boolean = false,
    override var state: String,
    override var createTimeMillis: Long = 0,
    /** 发送失败1、发送成功-1、正在发送0  */
    var smsStatus: Int = -1,
// 以下是本地状态
    /** 本地临时缓存的消息  */
    override var isLocal: Boolean = false,
    override var sending: Boolean = false,
    /** 是否发送成功  */
    override var sendFail: Boolean = false,
    /** 是否显示时间戳  */
    override var showTimestamp: Boolean = false,
    var image: Image
) : Message(
    id = id,
    isLocal = isLocal,
    sending = sending,
    sendFail = sendFail,
    mine = mine,
    state = state,
    createTimeMillis = createTimeMillis,
    showTimestamp = showTimestamp
),
    Parcelable {

    companion object {
        private const val serialVersionUID = 1025221798212317198L
        const val TYPE = "inboxMessageImage"

        fun fake(toUserID: String, mine: Boolean = false): ImageMessage {
            return ImageMessage(
                id = System.currentTimeMillis().toString(),
                toUserId = toUserID,
                mine = mine,
                state = Integer.toHexString(listOf(0, 5, 10).random()),
                createTimeMillis = System.currentTimeMillis(),
                /** 发送失败1、发送成功-1、正在发送0  */
                smsStatus = listOf(1, 1, 1, 1, 1, -1, 0).random(),
                // 以下是本地状态

                /** 本地临时缓存的消息  */
                isLocal = (0..1).random() == 1,
                sending = false,
                /** 是否发送成功  */
                sendFail = true,
                /** 是否显示时间戳  */
                showTimestamp = (0..1).random() == 1,
                image = Image.fake()
            )
        }
    }

    init {
        type = TYPE
    }
}