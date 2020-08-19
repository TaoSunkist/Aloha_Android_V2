package com.wealoha.social.beans.common

import com.wealoha.social.beans.ResultData

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2015-1-13 上午10:59:06
 */
class ApiEndpointData : ResultData() {
    @kotlin.jvm.JvmField
    var ip: List<String>? = null
    @kotlin.jvm.JvmField
    var imageTestUrlMap: Map<String, String>? = null
}