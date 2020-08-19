package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.common.CountData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.GET

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-12 上午10:29:05
 */
interface CountService {
    /**
     * 计数
     *
     * @param callback
     */
    @GET(ServerUrlImpl.COUNT)
    fun count(callback: Callback<Result<CountData?>?>?)
}