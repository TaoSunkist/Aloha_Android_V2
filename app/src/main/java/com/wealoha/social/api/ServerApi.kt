package com.wealoha.social.api

import com.wealoha.social.beans.*
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*
import retrofit.mime.TypedFile

interface ServerApi{
    @FormUrlEncoded
    @POST("/v1/feed/")
    fun getPosts(
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FeedGetData?>?>?
    )

    @FormUrlEncoded
    @POST("/v1/feed/user")
    fun getUserPosts(
        @Field("uid") userId: String?,
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FeedGetData?>?>?
    )

    /**
     * 单条Feed
     *
     * @param postId
     * @param callback
     */
    @GET("/v1/feed/detail")
    fun singleFeed(
        @Query("postId") postId: String?,
        callback: Callback<Result<FeedGetData?>?>?
    )

    /**
     * 赞
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("/v1/feed/like")
    fun praisePost(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 取消赞
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("/v1/feed/like/cancel")
    fun dislikePost(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 删除自己的feed
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("/v1/feed/delete")
    fun deletePost(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 喜欢列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET("/v1/feed/like")
    fun getPraiseList(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: Int,
        callback: Callback<Result<UserListGetData?>?>?
    )

    /**
     * 被赞过的post
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET("/v1/feed/liked")
    fun getPraisedList(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int,
        callback: Callback<Result<FeedGetData?>?>?
    )

    /**
     * 被圈过的post
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET("/v1/feed/tagedme")
    fun getTagedList(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int,
        callback: Callback<Result<FeedGetData?>?>?
    )

    @GET("/v1/hashtag/activity/list")
    fun getHashTag(
        @Query("latitude") latitude: String?,
        @Query("longitude") longitude: String?,
        callback: Callback<Result<FeedGetData?>?>?
    )

    /**
     * 话题页获取话题相关的post
     *
     * @param cursor
     * @param count
     * @param callback
     * @return void
     */
    @GET("/v1/hashtag/post")
    fun getTopicPosts(
        @Query("itemId") id: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: Int?,
        callback: Callback<Result<TopicPostResultData?>?>?
    )

    @GET("/v1/hashtag/activity/list")
    fun getHashTag(callback: Callback<Result<HashTagResultData?>?>?)
    // imageId
    // description 可选，需要限定长度(140)
    // style (string) 可选，使用的滤镜
    // vignette(true|false|yes|no) 可选，是否打开了暗角
    // venue 可选，地区id，地区列表数据从这里获取 VenueList
    // latitude 可选[-90,90]，取不到不要传这个参数
    // longitude 可选[-180,180]，取不到不要传这个参数，必须和latitude同时存在
    @FormUrlEncoded
    @POST("/v1/feed/publish/image")
    fun uploadFeed( //
        @Field("imageId") imageId: String?,  //
        @Field("description") description: String?,  //
        @Field("style") style: String?,  //
        @Field("venue") venue: String?,  //
        @Field("hashtagId") hashtagId: String?,  // 可选，使用tag活动的id
        @Field("latitude") latitude: Double?,  //
        @Field("longitude") longitude: Double?,  //
        @Field("tagAnchorX[]") tagAnchorX: Array<Float?>?,  //
        @Field("tagAnchorY[]") tagAnchorY: Array<Float?>?,  //
        @Field("tagCenterX[]") tagCenterX: Array<Float?>?,  //
        @Field("tagCenterY[]") tagCenterY: Array<Float?>?,  //
        @Field("tagUserId[]") tagUserId: Array<String?>?,  //
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 查看用户feed
     *
     * @param userId
     * 传null看用户自己的
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.LOAD_USER_FEED)
    fun userFeed(
        @Field("uid") userId: String?,
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?
    ): Result<FeedResult?>?

    @FormUrlEncoded
    @POST(ServerUrlImpl.LOAD_USER_FEED)
    fun userFeed(
        @Field("uid") userId: String?,
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FeedResult?>?>?
    )

    /**
     * 查看自己收到的feed
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.GET_TIME_LINE_FEED)
    fun feed(
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?
    ): Result<FeedResult?>?

    /**
     * 查看自己收到的feed
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.GET_TIME_LINE_FEED)
    fun feed(
        @Field("cursor") cursor: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FeedResult?>?>?
    )

    /**
     * 单条Feed
     *
     * @param postId
     * @param callback
     */
    @GET(ServerUrlImpl.FEED_DETAIL)
    fun feedDetail(
        @Query("postId") postId: String?,
        callback: Callback<Result<FeedResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.REPORT_FEED)
    fun reportFeed(
        @Field("postId") postId: String?,
        @Field("reason") reason: String?,
        @Field("type") type: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 赞
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.PRAISE_USER_FEED)
    fun praiseFeed(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 取消赞
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.DISLIKE_USER_FEED)
    fun dislikeFeed(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 删除自己的feed
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.DELETE_USER_FEED)
    fun deleteFeed(
        @Field("postId") postId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 喜欢列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET(ServerUrlImpl.GET_FEED_LIKE)
    fun likeFeedPersons(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: String?,
        callback: Callback<Result<UserListResult?>?>?
    )
    //
    // /**
    // * 通知列表
    // *
    // * @param cursor
    // * @param count
    // * @return
    // */
    // @GET(ServerUrlImpl.GET_NOTIFY)
    // public void getFeedNotify(@Query("cursor") String cursor, @Query("count")
    // String count, Callback<Result<NotifyResult>> callback);
    //
    // @GET(ServerUrlImpl.GET_NOTIFY_TWO)
    // public void getFeedNotifyTwo(@Query("cursor") String cursor,
    // @Query("count") String count, Callback<Result<NotifyResult>> callback);
    /**
     * @Description: 上传当个Feed
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-28
     */
    @Multipart
    @POST(ServerUrlImpl.UPLOAD_FEED_JPG)
    fun sendSingleFeed(
        @Part("image") file: TypedFile?,
        callback: Callback<Result<ImageUploadResult?>?>?
    )

    /**
     * @Title: removeTag
     * @Description: 移除圈人
     * @param @param file
     * @param @param callback 设定文件
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.DELETE_TAG)
    fun removeTag(
        @Field("postId") postId: String?,
        @Field("tagUserId") tagUserId: String?,
        callback: Callback<ResultData?>?
    )

    /**
     * @Title: removeTag
     * @Description: 移除圈人
     * @param @param file
     * @param @param callback 设定文件
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.FEED_SHARE)
    fun feedShare(
        @Field("postId") postId: String?,
        callback: Callback<ResultData?>?
    )

    /**
     * @Title: removeTag
     * @Description: 移除圈人
     * @param @param file
     * @param @param callback 设定文件
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.FEED_WEB_CALL_ALOHAAPP)
    fun feedWebCallAlohaApp(
        @Field("postId") postId: String?,
        @Field("shareByUserId") shareByUserId: String?,
        callback: Callback<ResultData?>?
    )

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