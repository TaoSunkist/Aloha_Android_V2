package com.wealoha.social.beans

import com.wealoha.social.beans.imagemap.HasImageMap

data class BlackListResult(
    var list: List<User>? = null,
    var nextCursorId: String? = null,
    override val imageMap: Map<String, Image>? = null

) : ResultData(), HasImageMap {

}