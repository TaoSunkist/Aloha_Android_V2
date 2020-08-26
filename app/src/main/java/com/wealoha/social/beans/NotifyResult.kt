package com.wealoha.social.beans

import com.wealoha.social.beans.*
import com.wealoha.social.beans.imagemap.HasImageMap

data class NotifyResult(
    var nextCursorId: String? = null,
    var postMap: Map<String, Feed>? = null,
    var commentCountMap: Map<String, Int>? = null,
    var likeCountMap: Map<String, Int>? = null,
    var userMap: Map<String, User>? = null,
    override var imageMap: Map<String, Image>? = null,
    var list: List<Notify>? = null
) : ResultData(), HasImageMap {

}