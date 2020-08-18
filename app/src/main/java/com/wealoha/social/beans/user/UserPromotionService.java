package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.GET;

import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * @author javamonk
 * @createTime 14-10-16 PM12:02
 */
public interface UserPromotionService {

	/**
	 * 获取邀请状态
	 * 
	 * @return
	 */
	@GET(ServerUrlImpl.GET_USER_PROMOTION)
	public Result<PromotionGetData> get();

	/**
	 * 获取邀请状态
	 * 
	 * @return
	 */
	@GET(ServerUrlImpl.GET_USER_PROMOTION)
	public void get(Callback<Result<PromotionGetData>> callback);
	// /**
	// * 新注册用户，提交邀请码使用
	// *
	// * @param code
	// * @return
	// */
	// @FormUrlEncoded
	// @POST(GlobalConstants.ServerUrl.LOAD_USER_FEED)
	// public Result<PromotionSetData> submit(@Field("code") String code);
}