package com.wealoha.social.api

import com.wealoha.social.beans.FeedResult
import com.wealoha.social.beans.ImageUploadResult
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.user.UserListResult
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*
import retrofit.mime.TypedFile

/**
 * Feed读取接口
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-13 下午12:29:18
 */
interface FeedService {
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
}