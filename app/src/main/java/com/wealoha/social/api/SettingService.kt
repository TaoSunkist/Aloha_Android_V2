package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.PushSettingResult
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.GET
import retrofit.http.POST

/**
 * @author javamonk
 * @createTime 14-10-11 PM5:50
 */
interface SettingService {
    /**
     * 读取当前设置
     *
     * @return
     */
    // TODO
    @get:GET(ServerUrlImpl.URL_USER_SETTING_PUSH)
    val pushSetting: Result<PushSettingResult?>?

    @GET(ServerUrlImpl.URL_USER_SETTING_PUSH)
    fun getPushSetting(callback: Callback<Result<PushSettingResult?>?>?)

    /**
     * 保存设置
     *
     * @param pushSound
     * @param pushVibration
     * @param pushShowDetail
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
    fun savePushSetting(
        @Field("pushSound") pushSound: Boolean?,  //
        @Field("pushVibration") pushVibration: Boolean?,  //
        @Field("pushShowDetail") pushShowDetail: Boolean?,  //
        @Field("pushPostLike") pushPostLike: String?,  //
        @Field("pushPostComment") pushPostComment: String?
    ): Result<ResultData?>?

    @FormUrlEncoded
    @POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
    fun savePushSetting(
        @Field("pushSound") pushSound: Boolean?,  //
        @Field("pushVibration") pushVibration: Boolean?,  //
        @Field("pushShowDetail") pushShowDetail: Boolean?,  //
        @Field("pushPostLike") pushPostLike: String?,  //
        @Field("pushPostComment") pushPostComment: String?,  //
        @Field("pushPostTag") pushPostTag: String?,  //
        callback: Callback<Result<ResultData?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
    fun savePushSetting(
        @Field("pushAloha") pushAloha: String?,  //
        callback: Callback<Result<ResultData?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
    fun savePushSetting(
        @Field("pushSound") pushSound: Boolean?,  //
        @Field("pushVibration") pushVibration: Boolean?,  //
        @Field("pushShowDetail") pushShowDetail: Boolean?
    ): Result<PushSettingResult?>?
}