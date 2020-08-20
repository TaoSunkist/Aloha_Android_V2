package com.wealoha.social.beans

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
class Comment2GetData : ResultData() {
    @kotlin.jvm.JvmField
	var nextCursorId: String? = null
    @kotlin.jvm.JvmField
	var userMap: Map<String, UserDTO>? = null
    @kotlin.jvm.JvmField
	var imageMap: Map<String, ImageCommonDto>? = null
    @kotlin.jvm.JvmField
	var list: List<CommentDTO>? = null
    @kotlin.jvm.JvmField
	var lateCursorId: String? = null
}