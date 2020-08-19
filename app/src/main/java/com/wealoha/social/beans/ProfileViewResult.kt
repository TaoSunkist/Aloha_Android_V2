package com.wealoha.social.beans

import com.wealoha.social.beans.feed.Feed
import com.wealoha.social.beans.imagemap.HasImageMap
import java.util.*

/**
 * Created by walker on 14-3-25.
 */
class ProfileViewResult : ResultData(), HasImageMap {
    var nextCursorId: String? = null
    var commentCountMap: HashMap<String, Int>? = null
    var likCountMap: HashMap<String, Int>? = null
    var userMap: HashMap<String, Any>? = null
    var list: ArrayList<Feed>? = null
    override var imageMap: Map<String, Image>? = null
}