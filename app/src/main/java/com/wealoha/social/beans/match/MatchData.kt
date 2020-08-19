package com.wealoha.social.beans.match

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User
import com.wealoha.social.beans.imagemap.HasImageMap
import java.io.Serializable

/**
 * Match调用结果，下一批待匹配的用户或者错误
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-29 下午4:44:17
 */
class MatchData : ResultData(), Serializable, HasImageMap {
    @kotlin.jvm.JvmField
    var list: List<User>? = null

    /** 还需要多少秒才能刷下一批  */
    @kotlin.jvm.JvmField
    var quotaResetSeconds = 0

    /** 每次刷一批的时间段长度  */
    var quotaDurationSeconds = 0

    /** 可用的重置配额次数  */
    @kotlin.jvm.JvmField
    var quotaReset = 0
    override var imageMap: Map<String, Image>? = null
    @kotlin.jvm.JvmField
    var recommendSourceMap: Map<String, String>? = null

    companion object {
        private const val serialVersionUID = -7473243341042126058L
        @kotlin.jvm.JvmField
        val TAG = MatchData::class.java.simpleName
    }
}