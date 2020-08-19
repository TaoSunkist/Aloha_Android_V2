package com.wealoha.social.api

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

/**
 * 用户注册
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-21 上午11:11:13
 */
interface UserRegisterService {
    /**
     * 获得短信验证码
     *
     * @param number
     * @param callback
     */
    @POST("/v1/user/register/mobile/verify")
    @FormUrlEncoded
    fun getCode(
        @Field("number") number: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 活得重设密码的短信验证码
     *
     * @param number
     * @param callback
     */
    @POST("/v1/user/password/mobile/reset/verify")
    @FormUrlEncoded
    fun getResetPasswordCode(
        @Field("number") number: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 提交注册
     *
     * @param number
     * @param code
     * @param passwordMd5
     * @param callback
     */
    @POST("/v1/user/register/mobile")
    @FormUrlEncoded
    fun register(
        @Field("number") number: String?,
        @Field("code") code: String?,  //
        @Field("passwordMd5") passwordMd5: String?,
        callback: Callback<Result<AuthData?>?>?
    )
}