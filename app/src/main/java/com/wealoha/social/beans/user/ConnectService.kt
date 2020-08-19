package com.wealoha.social.beans.user

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.Result
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

/**
 * 第三方登录
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午3:14:23
 */
interface ConnectService {
    /**
     * 连接微博
     *
     * @param uid
     * @param accessToken
     * @return
     */
    @POST(ServerUrlImpl.CONNECT_WEIBO)
    @FormUrlEncoded
    fun connectWeibo(
        @Field("uid") uid: String?,
        @Field("accessToken") accessToken: String?
    ): Result<AuthData?>?

    /**
     * 连接微博
     *
     * @param uid
     * @param accessToken
     * @param callback
     */
    @POST(ServerUrlImpl.CONNECT_WEIBO)
    @FormUrlEncoded
    fun connectWeibo(
        @Field("uid") uid: String?,
        @Field("accessToken") accessToken: String?,
        callback: Callback<Result<AuthData?>?>?
    )

    /**
     * 连接微博
     *
     * @param uid
     * @param accessToken
     * @param callback
     */
    @POST("/v1/connect/facebook")
    @FormUrlEncoded
    fun connectFacebook(
        @Field("uid") uid: String?,
        @Field("accessToken") accessToken: String?,
        callback: Callback<Result<AuthData?>?>?
    )
}