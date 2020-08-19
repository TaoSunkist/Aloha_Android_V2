package com.wealoha.social.beans.user

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午2:11:35
 */
interface AuthService {
    /**
     *
     * @param callback
     */
    @POST(ServerUrlImpl.LOGOUT_URL)
    fun unauth(callback: Callback<Result<ResultData?>?>?)

    // @FormUrlEncoded
    // @POST(GlobalConstants.ServerUrlPath.PUSH_UNBINDING)
    // Result<ResultData> bind(@Field("tokenGetui") String tokenGetui);
    @FormUrlEncoded
    @POST(ServerUrlImpl.PUSH_UNBINDING)
    fun unbind(
        @Field("token") token: String?,
        callback: Callback<Result<ResultData?>?>?
    )
}