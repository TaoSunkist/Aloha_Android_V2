package com.wealoha.social.beans

import java.io.Serializable

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
class Result<T : ResultData?> : Serializable {
    /**
     * @see me.cu.app.config.Constants.HttpStatus
     */
    @kotlin.jvm.JvmField
    var status = 0

    @kotlin.jvm.JvmField
    var data: T? = null// 外层没有错误(200)
    // 内存没有错误(0)

    /**
     * 响应是否ok
     *
     * @return
     */
    val isOk: Boolean
        get() =// 外层没有错误(200)
            // 内存没有错误(0)
            status == STATUS_CODE_OK && (data == null || data?.error == ResultData.Companion.ERROR_NO_ERROR)

    companion object {
        private const val serialVersionUID = 6540499880692897496L

        /* 成功 */
        const val STATUS_CODE_OK = 200

        /* 用戶已被封禁 */
        const val STATUS_CODE_FORBIDEN = 401

        /* 服務器錯誤 */
        const val STATUS_CODE_SERVER_ERROR = 500

        /* 參數錯誤 */
        const val STATUS_CODE_PARAM_ERROR = 501

        /** 请求太频繁  */
        const val STATUS_CODE_THRESHOLD_HIT = 503
    }
}