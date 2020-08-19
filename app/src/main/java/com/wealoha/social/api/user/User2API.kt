package com.wealoha.social.api.user

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.PromotionGetData
import retrofit.Callback
import retrofit.http.*

interface User2API {
    /**
     * 解除ALOHA
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("/v1/user/match/dislike")
    fun dislikeUser(
        @Field("userId") userId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * Aloha一个用户
     *
     * @param userId
     * @return
     */
    @POST("/v1/user/match/like")
    @FormUrlEncoded
    fun aloha(
        @Field("userId") userId: String?,
        @Field("refer") refer: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @GET("/v1/user/profile/view")
    fun userProfile(
        @Query("id") userId: String?,
        callback: Callback<Result<Profile2Data>?>
    )

    /***
     * 验证密码
     *
     * @param password
     * 密码
     * @return void
     */
    @POST("/v1/user/change/mobile/verifypassword")
    @FormUrlEncoded
    fun verifyPassword(
        @Field("password") password: String?,
        callback: Callback<Result<ResultData>?>
    )

    /***
     * 验证用户新手机号（接收新手机号，并生成验证码）
     *
     * @param number
     * 格式 +86 12345567890 (+国家码 空格 手机号)
     * @param callback
     * @return void 成功：返回空响应，可以继续注册 失败：验证码生成频率太快；手机号已注册 错误码： 200516 手机号已注册；用户已经登录 200520 手机号格式有问题 status=503
     * 请求频率太快 注意：生成频率是60秒
     */
    @POST("/v1/user/change/mobile/verify")
    @FormUrlEncoded
    fun mobileVerify(
        @Field("number") number: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 更改手机号
     *
     * @param number
     * @param callback
     * @return void
     */
    @POST("/v1/user/change/mobile")
    @FormUrlEncoded
    fun changeMobile(
        @Field("number") number: String?,
        @Field("password") password: String?,
        @Field("code") code: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @GET("/v1/user/setting/match")
    fun userMatchSetting(callback: Callback<Result<MatchSettingData>>)

    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @GET("/v1/user/promotion")
    fun getUserPromotionSetting(callback: Callback<Result<PromotionGetData?>?>?)

    @POST("/v1/user/setting/match")
    @FormUrlEncoded
    fun saveMatchSetting(
        @Field("filterRegion") filterRegion: String?,  //
        @Field("filterAgeRangeStart") filterAgeRangeStart: Int?,  //
        @Field("filterAgeRangeEnd") filterAgeRangeEnd: Int?,  //
        @Field("latitude ") latitude: Double?,  //
        @Field("longitude ") longitude: Double?,
        callback: Callback<Result<ResultData?>?>?
    )

    @POST("/v1/stat/land")
    @FormUrlEncoded
    fun startLand(
        @Field("guid") guid: String?,
        callback: Callback<Result<ResultData?>?>?
    )
}