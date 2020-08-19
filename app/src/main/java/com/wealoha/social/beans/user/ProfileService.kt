package com.wealoha.social.beans.user

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-21 上午11:28:33
 */
interface ProfileService {
    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @GET(ServerUrlImpl.USER_PROFILE_VIEW)
    fun view(@Query("id") userId: String?): Result<ProfileData?>?

    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @GET(ServerUrlImpl.USER_PROFILE_VIEW)
    fun view(
        @Query("id") userId: String?,
        callback: Callback<Result<ProfileData?>?>?
    )

    /**
     * 获取一个用户的profile
     *
     * @param userId
     * @return
     */
    @POST(ServerUrlImpl.USER_PROFILE_VIEW)
    @FormUrlEncoded
    fun getUserProfile(
        @Field("id") userId: String?,
        callback: Callback<Result<ProfileData?>?>?
    )

    /**
     * @Description: 记录名片点击
     * @param userId
     * @param callback
     * @see:
     * @since:
     * @description userId 名片用户 shareByUserId 分享名片的用户
     * @author: sunkist
     * @param callback
     * @date:2015-1-13
     */
    @POST(ServerUrlImpl.RECORD_CARD_CLICK)
    @FormUrlEncoded
    fun recordCardClick(
        @Field("userId") userId: String?,
        @Field("shareByUserId") shareByUserId: String?,
        callback: Callback<Result<ResultData?>?>?
    )
}