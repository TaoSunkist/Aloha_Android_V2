package com.wealoha.social.beans

/**
 * 话题功能部分请求新版的post 接口，新版post接口返回的数据和旧版中由postdto 转换后的post结构一致
 *
 * @author dell-pc
 */
class TopicPostResultData : ResultData() {
    /** 最新  */
    @kotlin.jvm.JvmField
    var list: List<TopicPostDTO>? = null

    /** 最热  */
    @kotlin.jvm.JvmField
    var hot: List<TopicPostDTO>? = null
    var imageMap: Map<String, ImageCommonDto>? = null
    var videoMap: Map<String, VideoCommonDTO>? = null
    var userMap: Map<String, UserDTO>? = null

    /** 最新的cursor  */
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
    @kotlin.jvm.JvmField
    var hashtag: HashTagDTO? = null
}