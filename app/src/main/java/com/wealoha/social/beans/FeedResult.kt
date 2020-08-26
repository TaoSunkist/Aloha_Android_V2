package com.wealoha.social.beans

import com.wealoha.social.beans.imagemap.HasImageMap

/**
 * @author javamonk
 * @createTime 14-10-13 AM10:29
 */
class FeedResult(
    var list: List<Feed>? = null,
    override var imageMap: Map<String, Image>? = null,
    var commentCountMap: Map<String, Int>? = null,
    var likeCountMap: Map<String, Int>? = null,
    var userMap: Map<String, User>? = null,
    var nextCursorId: String? = null
) : ResultData(), HasImageMap {

    /**
     * @Title: buildUserTags
     * @Description: 重新组装usertags 把user 对象加入usertags中, 判断feed中是否有我的tag
     * @param users
     * @param feeds
     * @return void
     * @throws
     */
    fun resetFeed(currentUser: User?) {
        if (list == null || userMap == null) {
            return
        }
        for (feed in list!!) {
            if (feed.userTags == null || feed.userTags!!.isEmpty()) {
                continue
            }
            for (userTag in feed.userTags!!) {
                // user 对象加入usertags中
                userTag.username = userMap!![userTag!!.tagUserId]!!.name
                userTag.tagUser = userMap!![userTag.tagUserId]
                // 是否有我的tag
                if (currentUser != null && userTag.tagUserId == currentUser.id) {
                    feed.tagMe = true
                    userTag.tagMe = true
                }
            }
        }
    }

}