package com.wealoha.social.beans

import com.wealoha.social.beans.User
import com.wealoha.social.impl.DynamicType

/**
 *
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-11-20
 */
@Deprecated("切换到Notify2")
class Notify(
    var postId: String? = null,
    var fromUser: String? = null,
    var type: String? = null,
    var createTimeMillis: Long = 0,
    var comment: String? = null,
    var replyUser: User? = null,
    var userIds: List<String>? = null,
    var updateTimeMillis: Long = 0,
    var unread: Boolean = false,
    var count: Int = 0,
    var replyMe: Boolean = false
) : DynamicType {

    override fun toString(): String {
        return "Notify [postId=$postId, fromUser=$fromUser, type=$type, createTimeMillis=$createTimeMillis, comment=$comment, replyUser=$replyUser, userIds=$userIds, updateTimeMillis=$updateTimeMillis, unread=$unread, count=$count, replyMe=$replyMe]"
    }

    /**
     * @Description:获取需要显示的视图类型
     * @see:
     * @since:
     * @description 暂时不能删除，涉及到 [FeedNoticeAdapter]
     * @author: sunkist
     * @date:2015-2-6
     */
    fun getTypeTag(): Int {
        return if (DynamicType.TYPE_NOTIFY_POST_LIKE == type) { // feed被点赞
            DynamicType.FEED_PRAISE_VIEW_TYPE
        } else if (DynamicType.TYPE_NOTIFY_POST_COMMENT == type) { // Feed被评论的Item显示
            DynamicType.FEED_COMMENT_VIEW_TYPE
        } else if (DynamicType.TYPE_NOTIFY_POST_NEWALOHA == type) { // 新人气的通知
            DynamicType.FEED_NEWALOHA_VIEW_TYPE
        } else if (DynamicType.TYPE_NOTIFY_POST_TAG == type) { // 圈人的通知
            DynamicType.FEED_POST_TAG_TYPE
        } else {
            -1
        }
    }
}