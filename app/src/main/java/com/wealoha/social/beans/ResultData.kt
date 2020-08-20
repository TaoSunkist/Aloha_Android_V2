package com.wealoha.social.beans

/**
 * API响应的result.getData()
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-27 下午10:45:34
 */
open class ResultData : IResultDataErrorCode {
    @kotlin.jvm.JvmField
    var error = 0

    companion object {
        const val ERROR_NO_ERROR = 0
    }
}