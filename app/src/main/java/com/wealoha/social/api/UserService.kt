package com.wealoha.social.api

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.ImageUploadResult
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.instagram.InstagramResult
import com.wealoha.social.beans.UserListResult
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*
import retrofit.mime.TypedFile

interface UserService {
    /**
     * 举报用户
     *
     * @param userId
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.REPORT_USER)
    fun reportUser(
        @Field("userId") userId: String?,
        @Field("type") type: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 举报用户
     *
     * @param userId
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.REPORT_USER)
    fun reportUser(
        @Field("userId") userId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 加入黑名单
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.BLACK_USER)
    fun blackUser(
        @Field("userId") userId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 解除ALOHA
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.CANCEL_ALOHA_USER)
    fun dislikeUser(
        @Field("userId") userId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 黑名单
     *
     * @param cursor
     * 可选
     * @param count
     * 可选
     * @return
     */
    @GET(ServerUrlImpl.BLACK_LIST)
    fun blackList(
        @Query("cursor") cursor: String?,
        @Query("count") count: String?,
        callback: Callback<Result<UserListResult?>?>?
    )

    /**
     * 喜欢列表
     *
     * @param cursor
     * 可选
     * @param limit
     * 可选
     * @return
     */
    @GET(ServerUrlImpl.LIKE_LIST)
    fun likeList(
        @Query("cursor") cursor: String?,
        @Query("count") limit: String?,
        callback: Callback<Result<UserListResult?>?>?
    )

    /**
     * 人气列表
     *
     * @param cursor
     * 可选
     * @param limit
     * 可选
     * @return
     */
    @GET(ServerUrlImpl.POPULARITY_LIST)
    fun popularityList(
        @Query("cursor") cursor: String?,
        @Query("count") limit: String?,
        callback: Callback<Result<UserListResult?>?>?
    )

    /**
     * 解除黑名单
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.UNBLOCK)
    fun unblock(
        @Field("userId") userId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 註冊登录
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.REGISTER_LOGIN)
    fun login(
        @Field("number") number: String?,
        callback: Callback<Result<AuthData?>?>?
    )

    /**
     * 登录
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.LOGIN)
    fun login(
        @Field("account") number: String?,
        @Field("password") password: String?,
        callback: Callback<Result<AuthData?>?>?
    )

    /**
     * 登录方式
     *
     * @param userId
     * @return
     */
    @GET(ServerUrlImpl.LOGIN_TYPE)
    fun loginType(callback: Callback<Result<InstagramResult?>?>?)

    /**
     * @Description:
     * @param number
     * @param newPassword
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-3-9
     */
    @POST(ServerUrlImpl.RESET_PASSWORD_USER)
    @FormUrlEncoded
    fun reqAlertPassWord(
        @Field("password") number: String?,
        @Field("newPassword") newPassword: String?,
        callback: Callback<Result<AuthData?>?>?
    )

    /**
     * @Description:
     * @param username
     * @param birthday
     * @param height
     * @param weight
     * @param selfTag
     * @param regionCode
     * @param selfPurpose
     * @param mImgid
     * @param summary
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-3-9
     */
    @POST(ServerUrlImpl.CHANGE_PROFILE)
    @FormUrlEncoded
    fun reqAlertUserInfo(
        @Field("name") username: String?,
        @Field("birthday") birthday: String?,  //
        @Field("height") height: String?,
        @Field("weight") weight: String?,  //
        @Field("selfTag") selfTag: String?,
        @Field("regionCode") regionCode: String?,  //
        @Field("selfPurpose") selfPurpose: String?,
        @Field("avatarImageId") mImgid: String?,  //
        @Field("summary") summary: String?,
        callback: Callback<Result<AuthData?>?>?
    )

    /**
     * @Description:上传图片,分两步进行操作.
     * @param file
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年7月14日
     */
    @Multipart
    @POST(ServerUrlImpl.UPLOAD_FEED_JPG)
    fun sendSingleFeed(
        @Part("image") file: TypedFile?,
        callback: Callback<Result<ImageUploadResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.CHANGE_PROFILE)
    fun uploadUserData(
        @Field("name") name: String?,
        @Field("height") height: String?,  //
        @Field("weight") weight: String?,
        @Field("birthday") birthday: String?,  //
        @Field("regionCode") regionCode: String?,
        @Field("avatarImageId") avatarImageId: String?,  //
        callback: Callback<Result<AuthData?>?>?
    )

    @FormUrlEncoded
    @POST("https://api.weibo.com/oauth2/access_token")
    fun authSinaAccount(
        @Field("name") name: String?,
        @Field("height") height: String?,  //
        @Field("weight") weight: String?,
        @Field("birthday") birthday: String?,  //
        @Field("regionCode") regionCode: String?,
        @Field("avatarImageId") avatarImageId: String?,  //
        callback: Callback<Result<AuthData?>?>?
    )
}