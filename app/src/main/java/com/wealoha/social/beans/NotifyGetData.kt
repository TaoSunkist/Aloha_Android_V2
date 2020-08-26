package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:33:05
 */
data class NotifyGetData(
    var commentCountMap: Map<String, Int>? = null,
    var likeCountMap: Map<String, Int>? = null,
    var userMap: Map<String, UserDTO>? = null,
    var imageMap: Map<String, ImageCommonDto>? = null,
    var postMap: Map<String, PostDTO>? = null,
    var list: List<AbsNotify2DTO>? = null,
    var nextCursorId: String? = null
) : ResultData() {

}