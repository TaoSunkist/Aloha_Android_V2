package com.wealoha.social.api.topic;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;

public interface TopicAPI {

	/**
	 * 获取活动的列表
	 * 
	 * @param userId
	 * @return
	 */
	@GET("/tag/list")
	public void getTopic(@Query("latitude") Double latitude,//
			@Query("longitude") Double longitude, //
			Callback<Result<TopicData>> callback);
}
