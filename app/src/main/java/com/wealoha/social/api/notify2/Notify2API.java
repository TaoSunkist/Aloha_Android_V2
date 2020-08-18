package com.wealoha.social.api.notify2;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:31:04
 */
public interface Notify2API {

	@GET("/v1/feed/notify2")
	public void getNotifies(@Query("cursor") String cursor, @Query("count") Integer count, @Query("aloha") Boolean aloha, Callback<Result<NotifyGetData>> callback);

	@POST("/v1/feed/notify2/clearUnread")
	public void clearUnread(Callback<Result<ResultData>> callback);

	@GET("/v1/feed/notify2/mergeUsers")
	public void getMergeUsers(@Query("notifyId") String notifyId, @Query("count") Integer count, @Query("cursor") String cursor, Callback<Result<MergeUsersGetData>> callback);
}
