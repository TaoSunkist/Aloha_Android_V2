package com.wealoha.social.beans

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
data class Comment2GetData(
    var nextCursorId: String? = null,
    var userMap: Map<String, UserDTO>? = null,
    var imageMap: Map<String, ImageCommonDto>? = null,
    var list: List<CommentDTO>? = null,
    var lateCursorId: String? = null
) : ResultData() {

}