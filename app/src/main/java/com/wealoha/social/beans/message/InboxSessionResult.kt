package com.wealoha.social.beans.message

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.imagemap.HasImageMap

/**
 * Created by walker on 14-3-25.
 */
class InboxSessionResult : ResultData(), HasImageMap {
    // Success
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null

    @kotlin.jvm.JvmField
    var list: List<InboxSession>? = null

    @kotlin.jvm.JvmField
    var newMessageMap: Map<String, Message> = mapOf()

    override var imageMap: Map<String, Image>? = null
}