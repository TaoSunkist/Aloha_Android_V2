package com.wealoha.social.beans

import com.wealoha.social.beans.*
import com.wealoha.social.beans.imagemap.HasImageMap

class NotifyResult : ResultData(), HasImageMap {
    var nextCursorId: String? = null
    @kotlin.jvm.JvmField
    var postMap: Map<String, Feed>? = null
    @kotlin.jvm.JvmField
    var commentCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var likeCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var userMap: Map<String, User>? = null
    override var imageMap: Map<String, Image>? = null
    @kotlin.jvm.JvmField
    var list: List<Notify>? = null
}