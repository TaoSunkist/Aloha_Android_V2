package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:33:05
 */
class NotifyGetData : ResultData() {
    @kotlin.jvm.JvmField
    var commentCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var likeCountMap: Map<String, Int>? = null
    @kotlin.jvm.JvmField
    var userMap: Map<String, UserDTO>? = null
    @kotlin.jvm.JvmField
    var imageMap: Map<String, ImageCommonDto>? = null
    @kotlin.jvm.JvmField
    var postMap: Map<String, PostDTO>? = null
    @kotlin.jvm.JvmField
    var list: List<AbsNotify2DTO>? = null
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
}