package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

/**
 *
 * @author hongwei
 * @createTime Jan 12, 2015 12:11:30 PM
 */
interface ClientLogService {
    @POST(ServerUrlImpl.CLIENT_LOG)
    @FormUrlEncoded
    fun log(
        @Field("message") message: String?,
        @Field("exception") exception: String?,  //
        @Field("timestamp") createTime: Long?
    ): Result<ResultData?>?
}