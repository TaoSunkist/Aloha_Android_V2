package com.wealoha.social.beans

import com.wealoha.social.beans.imagemap.HasImageMap

class BlackListResult : ResultData(), HasImageMap {
    @kotlin.jvm.JvmField
    var list: List<User>? = null
    var nextCursorId: String? = null
    override val imageMap: Map<String, Image>? =
        null

}