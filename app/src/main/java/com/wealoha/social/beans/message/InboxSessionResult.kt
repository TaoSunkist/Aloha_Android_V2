package com.wealoha.social.beans.message

import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.imagemap.HasImageMap

/**
 * Created by walker on 14-3-25.
 */
data class InboxSessionResult(
    var nextCursorId: String? = null,

    var list: List<InboxSession>? = null,

    var newMessageMap: Map<String, Message> = mapOf()
) : ResultData() {

    companion object {
        fun fake(): InboxSessionResult {
            val inboxSessionList = InboxSession.fakeForList()
            val newMessageMap = hashMapOf<String, Message>()
            val imageMap = hashMapOf<String, Image>()

            inboxSessionList.forEach {
                val user = it.user
                when (it.type) {
                    TextMessage.TYPE -> {
                        newMessageMap[user.id] = TextMessage.fake(toUserID = user.id)
                    }
                    ImageMessage.TYPE -> {
                        newMessageMap[user.id] = ImageMessage.fake(toUserID = user.id)
                    }
                }
            }
            return InboxSessionResult(
                nextCursorId = Direct.Early.value,
                list = inboxSessionList,
                newMessageMap = hashMapOf()
            )
        }
    }
}