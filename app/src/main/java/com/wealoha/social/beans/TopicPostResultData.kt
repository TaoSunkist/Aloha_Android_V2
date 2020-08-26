package com.wealoha.social.beans

/**
 * 话题功能部分请求新版的post 接口，新版post接口返回的数据和旧版中由postdto 转换后的post结构一致
 *
 * @author dell-pc
 */
class TopicPostResultData(
    var list: List<TopicPostDTO>? = null,

    /** 最热  */
    var hot: List<TopicPostDTO>? = null,
    var imageMap: Map<String, ImageCommonDto>? = null,
    var videoMap: Map<String, VideoCommonDTO>? = null,
    var userMap: Map<String, UserDTO>? = null,

    /** 最新的cursor  */
    var nextCursorId: String? = null,
    var hashtag: HashTagDTO? = null
) : ResultData() {

}