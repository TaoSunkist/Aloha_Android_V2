package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-21 上午11:28:33
 */
public interface ProfileService {

	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@GET(ServerUrlImpl.USER_PROFILE_VIEW)
	public Result<ProfileData> view(@Query("id") String userId);

	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@GET(ServerUrlImpl.USER_PROFILE_VIEW)
	public void view(@Query("id") String userId, Callback<Result<ProfileData>> callback);
	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@POST(ServerUrlImpl.USER_PROFILE_VIEW)
	@FormUrlEncoded
	public void getUserProfile(@Field("id") String userId, Callback<Result<ProfileData>> callback);
	
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
	public void recordCardClick(@Field("userId") String userId, @Field("shareByUserId") String shareByUserId, retrofit.Callback<Result<ResultData>> callback);

}
