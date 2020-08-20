package com.wealoha.social.api.post;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.wealoha.social.beans.HashTagResultData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.TopicPostResultData;

public interface PostAPI {

	/**
	 * 话题页获取话题相关的post
	 * 
	 * @param cursor
	 * @param count
	 * @param callback
	 * @return void
	 */
	@GET("/v1/hashtag/post")
	public void getTopicPosts(@Query("itemId") String id, @Query("cursor") String cursor, @Query("count") Integer count, Callback<Result<TopicPostResultData>> callback);

	@GET("/v1/hashtag/activity/list")
	public void getHashTag(Callback<Result<HashTagResultData>> callback);
	
}
