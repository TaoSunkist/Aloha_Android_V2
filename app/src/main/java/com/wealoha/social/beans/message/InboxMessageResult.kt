package com.wealoha.social.beans.message

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User
import com.wealoha.social.beans.imagemap.HasImageMap

/**
 * Match调用结果，下一批待匹配的用户或者错误
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-29 下午4:44:17
 */
class InboxMessageResult(
    var nextCursorId: String? = null,
    var messageId: String? = null,
    var toUser: User? = null,
    var user: User? = null,
    var list: List<Message>? = null,
    override var imageMap: Map<String, Image>? = null
) : ResultData(), HasImageMap {

}