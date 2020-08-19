package com.wealoha.social.api;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.UserListGetData;

public interface Feed2API {

	@FormUrlEncoded
	@POST("/v1/feed/")
	public void getPosts(@Field("cursor") String cursor, @Field("count") Integer count, Callback<Result<FeedGetData>> callback);

	@FormUrlEncoded
	@POST("/v1/feed/user")
	public void getUserPosts(@Field("uid") String userId, @Field("cursor") String cursor, @Field("count") Integer count, Callback<Result<FeedGetData>> callback);

	/**
	 * 单条Feed
	 * 
	 * @param postId
	 * @param callback
	 */
	@GET("/v1/feed/detail")
	public void singleFeed(@Query("postId") String postId, Callback<Result<FeedGetData>> callback);

	/**
	 * 赞
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST("/v1/feed/like")
	public void praisePost(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	/**
	 * 取消赞
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST("/v1/feed/like/cancel")
	public void dislikePost(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	/**
	 * 删除自己的feed
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST("/v1/feed/delete")
	public void deletePost(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	@FormUrlEncoded
	@POST("/v1/feed/report")
	public void reportFeed(@Field("postId") String postId, @Field("reason") String reason, @Field("type") String type, Callback<Result<ResultData>> callback);

	/**
	 * @Title: removeTag
	 * @Description: 移除圈人
	 * @param callback
	 */
	@FormUrlEncoded
	@POST("/v1/feed/delete/tag")
	public void removeTag(@Field("postId") String postId, @Field("tagUserId") String tagUserId, Callback<Result<ResultData>> callback);

	/**
	 * 喜欢列表
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET("/v1/feed/like")
	public void getPraiseList(@Query("postId") String postId, @Query("cursor") String cursor, @Query("count") int count, Callback<Result<UserListGetData>> callback);

	/**
	 * 被赞过的post
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET("/v1/feed/liked")
	public void getPraisedList(@Query("cursor") String cursor, @Query("count") int count, Callback<Result<FeedGetData>> callback);

	/**
	 * 被圈过的post
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET("/v1/feed/tagedme")
	public void getTagedList(@Query("cursor") String cursor, @Query("count") int count, Callback<Result<FeedGetData>> callback);

	@GET("/v1/hashtag/activity/list")
	public void getHashTag(@Query("latitude") String latitude, @Query("longitude") String longitude, Callback<Result<FeedGetData>> callback);
}
