package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.common.ApiEndpointData
import com.wealoha.social.beans.common.ConstantsData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.GET

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午5:51:11
 */
interface ConstantsService {
    @GET(ServerUrlImpl.CONSTANTS)
    operator fun get(callback: Callback<Result<ConstantsData?>?>?)

    /**
     * API可用的入口
     *
     * @return
     */
    @GET(ServerUrlImpl.CONSTANTS_API_ENDPOINT)
    fun apiEndpoing(): Result<ApiEndpointData?>?
}