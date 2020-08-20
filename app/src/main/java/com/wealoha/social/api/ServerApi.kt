package com.wealoha.social.api

import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.UserListGetData
import retrofit.Callback
import retrofit.http.*

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

    @FormUrlEncoded
    @POST("/v1/feed/report")
    fun reportFeed(
        @Field("postId") postId: String?,
        @Field("reason") reason: String?,
        @Field("type") type: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * @Title: removeTag
     * @Description: 移除圈人
     * @param callback
     */
    @FormUrlEncoded
    @POST("/v1/feed/delete/tag")
    fun removeTag(
        @Field("postId") postId: String?,
        @Field("tagUserId") tagUserId: String?,
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
}