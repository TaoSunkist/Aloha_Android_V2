package com.wealoha.social.api.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.user.PromotionGetData;

public interface User2API {

	/**
	 * 解除ALOHA
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST("/v1/user/match/dislike")
	public void dislikeUser(@Field("userId") String userId, Callback<Result<ResultData>> callback);

	/**
	 * Aloha一个用户
	 * 
	 * @param userId
	 * @return
	 */
	@POST("/v1/user/match/like")
	@FormUrlEncoded
	public void aloha(@Field("userId") String userId, @Field("refer") String refer, Callback<Result<ResultData>> callback);

	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@GET("/v1/user/profile/view")
	public void userProfile(@Query("id") String userId, Callback<Result<Profile2Data>> callback);

	/***
	 * 验证密码
	 * 
	 * @param password
	 *            密码
	 * @return void
	 */
	@POST("/v1/user/change/mobile/verifypassword")
	@FormUrlEncoded
	public void verifyPassword(@Field("password") String password, Callback<Result<ResultData>> callback);

	/***
	 * 验证用户新手机号（接收新手机号，并生成验证码）
	 * 
	 * @param number
	 *            格式 +86 12345567890 (+国家码 空格 手机号)
	 * @param callback
	 * @return void 成功：返回空响应，可以继续注册 失败：验证码生成频率太快；手机号已注册 错误码： 200516 手机号已注册；用户已经登录 200520 手机号格式有问题 status=503
	 *         请求频率太快 注意：生成频率是60秒
	 */
	@POST("/v1/user/change/mobile/verify")
	@FormUrlEncoded
	public void mobileVerify(@Field("number") String number, Callback<Result<ResultData>> callback);

	/**
	 * 更改手机号
	 * 
	 * @param number
	 * @param callback
	 * @return void
	 */
	@POST("/v1/user/change/mobile")
	@FormUrlEncoded
	public void changeMobile(@Field("number") String number, @Field("password") String password, @Field("code") String code, Callback<Result<ResultData>> callback);

	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@GET("/v1/user/setting/match")
	public void userMatchSetting(Callback<Result<MatchSettingData>> callback);

	/**
	 * 获取一个用户的profile
	 * 
	 * @param userId
	 * @return
	 */
	@GET("/v1/user/promotion")
	public void getUserPromotionSetting(Callback<Result<PromotionGetData>> callback);

	@POST("/v1/user/setting/match")
	@FormUrlEncoded
	public void saveMatchSetting(@Field("filterRegion") String filterRegion,//
			@Field("filterAgeRangeStart") Integer filterAgeRangeStart,//
			@Field("filterAgeRangeEnd") Integer filterAgeRangeEnd,//
			@Field("latitude ") Double latitude,//
			@Field("longitude ") Double longitude, Callback<Result<ResultData>> callback);

	@POST("/v1/stat/land")
	@FormUrlEncoded
	public void startLand(@Field("guid") String guid, Callback<Result<ResultData>> callback);
}
