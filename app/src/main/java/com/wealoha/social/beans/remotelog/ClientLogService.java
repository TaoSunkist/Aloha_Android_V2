package com.wealoha.social.beans.remotelog;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * @author hongwei
 * @createTime Jan 12, 2015 12:11:30 PM
 */
public interface ClientLogService {

	@POST(ServerUrlImpl.CLIENT_LOG)
	@FormUrlEncoded
	public Result<ResultData> log(@Field("message") String message, @Field("exception") String exception, //
			@Field("timestamp") Long createTime);

}
