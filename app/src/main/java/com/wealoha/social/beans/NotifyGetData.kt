package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:33:05
 */
data class NotifyGetData(
    val commentCountMap: Map<String, Int> = mapOf(),
    val likeCountMap: Map<String, Int> = mapOf(),
    val userMap: Map<String, UserDTO> = mapOf(),
    val imageMap: Map<String, ImageCommonDto> = mapOf(),
    val videoMap: Map<String, VideoCommonDTO> = mapOf(),
    val postMap: Map<String, PostDTO> = mapOf(),
    val list: List<AbsNotify2DTO> = listOf(),
    val nextCursorId: String = Direct.Late.value
) : ResultData() {

}