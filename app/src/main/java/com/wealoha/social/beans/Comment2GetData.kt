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
    var nextCursorId: String = Direct.Early.value,
    var userMap: Map<String, UserDTO> = mapOf(),
    var imageMap: Map<String, ImageCommonDto> = mapOf(),
    var list: List<CommentDTO> = listOf(),
    var lateCursorId: String = Direct.Late.value
) : ResultData() {

}