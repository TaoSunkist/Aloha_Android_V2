package com.wealoha.social.api

import com.wealoha.social.beans.*
import com.wealoha.social.beans.instagram.AccessToken
import com.wealoha.social.beans.instagram.InstagramResult
import com.wealoha.social.beans.message.InboxMessageResult
import com.wealoha.social.beans.message.InboxSessionResult
import com.wealoha.social.beans.message.UnreadData
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

    @POST(ServerUrlImpl.CLIENT_LOG)
    @FormUrlEncoded
    fun log(
        @Field("message") message: String?,
        @Field("exception") exception: String?,  //
        @Field("timestamp") createTime: Long?
    ): Result<ResultData?>?

    /**
     * @Description:从通知列表进入的Comment(第一次请求), 客户端传入参数postId, cursor, count, needCtx=true：
     * 返回cursor上下各一半count的数据list, preCursorId(向前|上)游标,
     * nextCursorId(向后|下)游标
     * @param postId
     * @param cursor
     * @param count
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年3月11日
     */
    @GET(ServerUrlImpl.GET_COMMENT)
    fun getFirstComment2s(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,  //
        @Query("count") count: Int,  //
        @Query("needCtx") needCtx: Boolean,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * @Description:客户端继续取数据， 传入postId, cursor, count, direct=[desc(向下)|asc(向上)]
     * 返回cursor在direct方向上的count条记录list和在次方向上的nextCursorId
     * @param postId
     * @param cursor
     * @param count
     * @param direct
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年3月11日
     */
    @GET(ServerUrlImpl.GET_COMMENT)
    fun byDirectGetComment2s(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,  //
        @Query("count") count: Int,  //
        @Query("direct") direct: String?,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * @Title: postComment
     * @Description: 发送评论
     * @param @param postId
     * @param @param userid
     * @param @param comment
     * @param @param callback 设定文件
     * @return void 返回类型
     * @throws
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT)
    fun sendComment(
        @Field("postId") postId: String?,  //
        @Field("replyUserId") userid: String?,  //
        @Field("comment") comment: String?,  //
        callback: Callback<Result<Comment2GetData?>?>?
    )
    /**
     * æŸ¥çœ‹ç”¨æˆ·feed
     *
     * @param userId
     * ä¼ nullçœ‹ç”¨æˆ·è‡ªå·±çš„
     * @param cursor
     * @param count
     * @return
     */
    @GET(ServerUrlImpl.GET_COMMENT)
    fun comments(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: String?,
        callback: Callback<Result<CommentResult?>?>?
    )

    /**
     * 发表评论(旧)
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT)
    fun postComment(
        @Field("postId") postId: String?,
        @Field("replyUserId") userid: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * 发表评论(新)
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT_V2)
    fun postCommentV2(
        @Field("postId") postId: String?,
        @Field("replyCommentId") replyCommentId: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * 旧版本的发表评论接口
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT)
    fun postCommentForOld(
        @Field("postId") postId: String?,
        @Field("replyUserId") userid: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<CommentResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.DELETE_COMMENT)
    fun deleteComment(
        @Field("postId") postId: String?,
        @Field("commentId") comment: String?,
        callback: Callback<Result<ResultData?>?>?
    )

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

    /**
     * 获取活动的列表
     *
     *
     * userId
     *
     * @return
     */
    @GET("/tag/list")
    fun getTopic(
        @Query("latitude") latitude: Double?,  //
        @Query("longitude") longitude: Double?,  //
        callback: Callback<Result<TopicData?>?>?
    )

    /**
     * 获取邀请状态
     *
     * @return
     */
    @GET(ServerUrlImpl.GET_USER_PROMOTION)
    fun get(): Result<PromotionGetData?>?

    /**
     * 获取邀请状态
     *
     * @return
     */
    @GET(ServerUrlImpl.GET_USER_PROMOTION)
    operator fun get(callback: Callback<Result<PromotionGetData?>?>?) // /**
    // * 新注册用户，提交邀请码使用
    // *
    // * @param code
    // * @return
    // */
    // @FormUrlEncoded
    // @POST(GlobalConstants.ServerUrl.LOAD_USER_FEED)
    // public Result<PromotionSetData> submit(@Field("code") String code);

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

    //
    // /v1/user/setting/privacy

    //
    // /v1/user/setting/privacy
    /**
     * @param callback
     * @author: sunkist
     * @description: matchExcludeDistanceKm
     * 不要在多近的范围内推荐我。空: 没有设置；大于200，不推荐
     * @date:2015年8月4日
     */
    @GET("/v1/user/setting/privacy")
    fun getPrivacy(callback: Callback<Result<PrivacyData?>?>?)

    /**
     * @param matchExcludeDistanceKm 1-1000有效，不传抹掉；大于200隐身
     * @param callback
     * @author: sunkist
     * @description:
     * @date:2015年8月4日
     */
    @POST("/v1/user/setting/privacy")
    @FormUrlEncoded
    fun setPrivacy(
        @Field("matchExcludeDistanceKm") matchExcludeDistanceKm: Int?,
        callback: Callback<Result<PrivacyData?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_INSTAGRAM_TOKEN)
    fun postToken(
        @Field("uid") uid: String?,  //
        @Field("accessToken") accessToken: String?,  //
        @Field("month") month: Int?,  //
        callback: Callback<Result<AuthData?>?>?
    )

    @POST(ServerUrlImpl.UNBIND_INSTAGRAM)
    fun unbind(callback: Callback<Result<AuthData?>?>?)

    @FormUrlEncoded
    @POST(ServerUrlImpl.SET_AUTO)
    fun setAuto(
        @Field("autoSync") autoSync: Boolean,  //
        callback: Callback<Result<AuthData?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.LOCATION)
    fun location( //
        @Field("name") uid: String?,  //
        @Field("coordinate") coordinate: String?,  //
        @Field("latitude") latitude: Double?,  //
        @Field("longitude") longitude: Double?,  //
        @Field("count") count: Int?,  //
        @Field("cursor") cursor: String?,  //
        callback: Callback<Result<LocationResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.LOCATION_RECORD)
    fun locationRecord( //
        @Field("latitude") latitude: Double?,  //
        @Field("longitude") longitude: Double?,
        callback: Callback<Result<ResultData?>?>?
    )

    @FormUrlEncoded
    @POST("/v1/stat/logdata")
    fun logData(
        @Field("data") dataStr: String?,
        callback: Callback<Result<ResultData?>?>?
    )
    /**
     * 通知列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.FIND_YOU)
    fun findYou(
        @Field("keyword") keyword: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FindYouResult?>?>?
    )

    /**
     * 默认圈人列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET(ServerUrlImpl.TAG_SUGGEST)
    fun defaultTagUsers(callback: Callback<Result<FindYouResult?>?>?)

    /**
     * 返回数据
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findRandom(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Result<MatchData?>?

    /**
     * 返回数据
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findRandom(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        callback: Callback<Result<MatchData?>?>?
    )

    /**
     * 重置配额，并且返回数据
     *
     * @param resetQuota
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findWithResetQuota(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("resetQuota") resetQuota: Boolean
    ): Result<MatchData?>?
    /**
     * Aloha一个用户
     *
     * @param userId
     * @return
     */
    // @POST(ServerUrlImpl.ALOHA_LIKE)
    // @FormUrlEncoded
    // public Result<ResultData> like(@Field("userId") String userId);
    /**
     * Aloha一个用户
     *
     * @param userId
     * @return
     */
    @POST(ServerUrlImpl.ALOHA_LIKE)
    @FormUrlEncoded
    fun like(
        @Field("userId") userId: String?,
        @Field("refer") refer: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    @POST(ServerUrlImpl.ALOHA_LIKE)
    @FormUrlEncoded
    fun like(
        @Field("userId") userId: String?,
        @Field("refer") refer: String?
    ): Result<ResultData?>?

    /**
     * Nope一个用户
     *
     * @param userId
     * @return
     */
    @POST(ServerUrlImpl.ALOHA_DISLIKE)
    @FormUrlEncoded
    fun dislike(@Field("userId") userId: String?): Result<ResultData?>?

    /**
     * @Description:
     * @param filterRegion
     * regions里选择的key或者空(可以不传，产品叫做『智能推荐』)
     * @param filterAgeRangeStart
     * 年龄区间，无不限制不传
     * @param filterAgeRangeEnd
     * 年龄区间，无不限制不传
     *
     * @return
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年7月9日
     */
    @POST("/v1/user/setting/match")
    @FormUrlEncoded
    fun reqRegions(
        @Field("filterRegion") filterRegion: String?,  //
        @Field("filterAgeRangeStart") filterAgeRangeStart: Int,  //
        @Field("filterAgeRangeEnd") filterAgeRangeEnd: Int,  //
        callback: Callback<Result<RegionResult?>?>?
    )

    @GET(ServerUrlImpl.GET_THE_SESSION_LIST)
    fun sessions(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int
    ): Result<InboxSessionResult?>?

    @GET(ServerUrlImpl.GET_THE_SESSION_LIST)
    fun sessions(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    @GET(ServerUrlImpl.GET_SMS_LIST)
    fun sessionMessages(
        @Query("sessionId") sessionId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: Int
    ): Result<InboxMessageResult?>?

    /**
     * 获取单个会话
     *
     * @param sessionId
     * 对方用户id
     * @return
     */
    @GET(ServerUrlImpl.GET_SINGLE_SESSION)
    fun getInboxSession(
        @Query("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * 创建一个会话
     *
     * @param sessionId
     * 要建立聊天的用户
     */
    @POST(ServerUrlImpl.CREATE_SESSION)
    @FormUrlEncoded
    fun post(
        @Field("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * 清理会话未读数
     *
     * @param sessionId
     * @param callback
     */
    @POST(ServerUrlImpl.INBOX_SESSION_CLEAR_UNREAD)
    @FormUrlEncoded
    fun clearUnread(
        @Field("sessionId") sessionId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * @Description:清理未读数不需要
     * @param sessionId
     * @return
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-2-11
     */
    @POST(ServerUrlImpl.INBOX_SESSION_CLEAR_UNREAD)
    @FormUrlEncoded
    fun clearUnread(@Field("sessionId") sessionId: String?): Result<ResultData?>?

    /**
     * 清理会话未读数
     *
     * @param sessionId
     * @param callback
     */
    @POST(ServerUrlImpl.PUSH_BINDING)
    @FormUrlEncoded
    fun bindPushToUser(
        @Field("token") token: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 取未读会话数
     *
     * @param callback
     */
    @GET(ServerUrlImpl.INBOX_SESSION_UNREAD)
    fun unread(callback: Callback<Result<UnreadData?>?>?)

    /**
     * 获取单个session
     *
     * @param sessionId
     * @param callback
     */
    @GET(ServerUrlImpl.INBOX_SESSION_GET)
    fun sessionGet(
        @Query("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * @Description: Multipart 中的Part使用 RestAdapter 的转换器来转换，也可以实现 TypedOutput
     * 来自己处理序列化。
     * @param token
     * @param callback
     * @see:
     * @since:
     * @description 聊天发送信息[图片]
     * @author: sunkist
     * @date:2014-12-30
     */
    @Multipart
    @POST(ServerUrlImpl.SEND_TO_USER_IMG)
    fun sendMsgImage(
        @Part("image") file: TypedFile?,
        @Part("toUserId") toUserId: String?,
        @Part("state") state: String?,
        callback: Callback<Result<InboxMessageResult?>?>?
    )

    /**
     * @Description:
     * @param token
     * @param callback
     * @see:
     * @since:
     * @description 聊天发送信息[信息]
     * @author: sunkist
     * @date:2014-12-30
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.SEND_TEXT_SMS)
    fun sendMsgText(
        @Field("text") text: String?,
        @Field("toUserId") toUserId: String?,
        @Field("state") state: String?,
        callback: Callback<Result<InboxMessageResult?>?>?
    )

    /**
     * @Description:删除单条消息
     * @param text
     * @param toUserId
     * @param state
     * @param callback
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-5
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.DEL_SINGLE_SMS)
    fun delSingleSms(
        @Field("sessionId") sessionId: String?,
        @Field("messageId") messageId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    @GET("/v1/feed/notify2")
    fun getNotifies(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int?,
        @Query("aloha") aloha: Boolean?,
        callback: Callback<Result<NotifyGetData?>?>?
    )

    @POST("/v1/feed/notify2/clearUnread")
    fun clearUnread(callback: Callback<Result<ResultData?>?>?)

    @GET("/v1/feed/notify2/mergeUsers")
    fun getMergeUsers(
        @Query("notifyId") notifyId: String?,
        @Query("count") count: Int?,
        @Query("cursor") cursor: String?,
        callback: Callback<Result<MergeUsersGetData?>?>?
    )

    @POST("/oauth/access_token")
    @FormUrlEncoded
    fun accessToken(
        @Field("client_id") clientId: String?,  //
        @Field("client_secret") clientSecret: String?,  //
        @Field("grant_type") grant_type: String?,  //
        @Field("redirect_uri") redirectUri: String?,  //
        @Field("code") code: String?
    ): AccessToken?

    @POST("/oauth/access_token")
    @FormUrlEncoded
    fun accessToken(
        @Field("client_id") clientId: String?,  //
        @Field("client_secret") clientSecret: String?,  //
        @Field("grant_type") grant_type: String?,  //
        @Field("redirect_uri") redirectUri: String?,  //
        @Field("code") code: String?,  //
        callback: Callback<AccessToken?>?
    )

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