package com.wealoha.social.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.TopicData;

public interface TopicAPI {

    /**
     * 获取活动的列表
     * <p>
     * userId
     *
     * @return
     */
    @GET("/tag/list")
    public void getTopic(@Query("latitude") Double latitude,//
                         @Query("longitude") Double longitude, //
                         Callback<Result<TopicData>> callback);
}
