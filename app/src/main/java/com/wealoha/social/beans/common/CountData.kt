package com.wealoha.social.beans.common

import com.wealoha.social.beans.ResultData

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-12 上午10:31:50
 */
class CountData : ResultData() {
    @kotlin.jvm.JvmField
    var newNotifyCount = 0
    @kotlin.jvm.JvmField
    var newFeed = false
}