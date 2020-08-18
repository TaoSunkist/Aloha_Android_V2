package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.instagram.InstagramResult;
import com.wealoha.social.impl.ServerUrlImpl;

public interface UserService {

	/**
	 * 举报用户
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.REPORT_USER)
	public void reportUser(@Field("userId") String userId, @Field("type") String type, Callback<Result<ResultData>> callback);

	/**
	 * 举报用户
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.REPORT_USER)
	public void reportUser(@Field("userId") String userId, Callback<Result<ResultData>> callback);

	/**
	 * 加入黑名单
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.BLACK_USER)
	public void blackUser(@Field("userId") String userId, Callback<Result<ResultData>> callback);

	/**
	 * 解除ALOHA
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.CANCEL_ALOHA_USER)
	public void dislikeUser(@Field("userId") String userId, Callback<Result<ResultData>> callback);

	/**
	 * 黑名单
	 * 
	 * @param cursor
	 *            可选
	 * @param count
	 *            可选
	 * @return
	 */
	@GET(ServerUrlImpl.BLACK_LIST)
	public void blackList(@Query("cursor") String cursor, @Query("count") String count, Callback<Result<UserListResult>> callback);

	/**
	 * 喜欢列表
	 * 
	 * @param cursor
	 *            可选
	 * @param limit
	 *            可选
	 * @return
	 */
	@GET(ServerUrlImpl.LIKE_LIST)
	public void likeList(@Query("cursor") String cursor, @Query("count") String limit, Callback<Result<UserListResult>> callback);

	/**
	 * 人气列表
	 * 
	 * @param cursor
	 *            可选
	 * @param limit
	 *            可选
	 * @return
	 */
	@GET(ServerUrlImpl.POPULARITY_LIST)
	public void popularityList(@Query("cursor") String cursor, @Query("count") String limit, Callback<Result<UserListResult>> callback);

	/**
	 * 解除黑名单
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.UNBLOCK)
	public void unblock(@Field("userId") String userId, Callback<Result<ResultData>> callback);

	/**
	 * 註冊登录
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.REGISTER_LOGIN)
	public void login(@Field("number") String number, Callback<Result<AuthData>> callback);

	/**
	 * 登录
	 * 
	 * @param userId
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.LOGIN)
	public void login(@Field("account") String number, @Field("password") String password, Callback<Result<AuthData>> callback);

	/**
	 * 登录方式
	 * 
	 * @param userId
	 * @return
	 */
	@GET(ServerUrlImpl.LOGIN_TYPE)
	public void loginType(Callback<Result<InstagramResult>> callback);

	/**
	 * @Description:
	 * @param number
	 * @param newPassword
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-3-9
	 */
	@POST(ServerUrlImpl.RESET_PASSWORD_USER)
	@FormUrlEncoded
	public void reqAlertPassWord(@Field("password") String number, @Field("newPassword") String newPassword, Callback<Result<AuthData>> callback);

	/**
	 * @Description:
	 * @param username
	 * @param birthday
	 * @param height
	 * @param weight
	 * @param selfTag
	 * @param regionCode
	 * @param selfPurpose
	 * @param mImgid
	 * @param summary
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-3-9
	 */
	@POST(ServerUrlImpl.CHANGE_PROFILE)
	@FormUrlEncoded
	public void reqAlertUserInfo(@Field("name") String username, @Field("birthday") String birthday, //
			@Field("height") String height, @Field("weight") String weight,//
			@Field("selfTag") String selfTag, @Field("regionCode") String regionCode,//
			@Field("selfPurpose") String selfPurpose, @Field("avatarImageId") String mImgid,//
			@Field("summary") String summary, Callback<Result<AuthData>> callback);

	/**
	 * @Description:上传图片,分两步进行操作.
	 * @param file
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月14日
	 */
	@Multipart
	@POST(ServerUrlImpl.UPLOAD_FEED_JPG)
	public void sendSingleFeed(@Part("image") TypedFile file, Callback<Result<ImageUploadResult>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.CHANGE_PROFILE)
	public void uploadUserData(@Field("name") String name, @Field("height") String height,//
			@Field("weight") String weight, @Field("birthday") String birthday, //
			@Field("regionCode") String regionCode, @Field("avatarImageId") String avatarImageId,//
			Callback<Result<AuthData>> callback);
	
	@FormUrlEncoded
	@POST("https://api.weibo.com/oauth2/access_token")
	public void authSinaAccount(@Field("name") String name, @Field("height") String height,//
			@Field("weight") String weight, @Field("birthday") String birthday, //
			@Field("regionCode") String regionCode, @Field("avatarImageId") String avatarImageId,//
			Callback<Result<AuthData>> callback);
}
