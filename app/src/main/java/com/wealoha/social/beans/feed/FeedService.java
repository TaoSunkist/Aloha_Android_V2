package com.wealoha.social.beans.feed;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import com.wealoha.social.beans.FeedResult;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.user.UserListResult;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * Feed读取接口
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-13 下午12:29:18
 */
public interface FeedService {

	// imageId
	// description 可选，需要限定长度(140)
	// style (string) 可选，使用的滤镜
	// vignette(true|false|yes|no) 可选，是否打开了暗角
	// venue 可选，地区id，地区列表数据从这里获取 VenueList
	// latitude 可选[-90,90]，取不到不要传这个参数
	// longitude 可选[-180,180]，取不到不要传这个参数，必须和latitude同时存在

	@FormUrlEncoded
	@POST("/v1/feed/publish/image")
	public void uploadFeed(//
			@Field("imageId") String imageId,//
			@Field("description") String description,//
			@Field("style") String style,//
			@Field("venue") String venue,//
			@Field("hashtagId") String hashtagId,// 可选，使用tag活动的id
			@Field("latitude") Double latitude,//
			@Field("longitude") Double longitude,//
			@Field("tagAnchorX[]") Float[] tagAnchorX,//
			@Field("tagAnchorY[]") Float[] tagAnchorY,//
			@Field("tagCenterX[]") Float[] tagCenterX,//
			@Field("tagCenterY[]") Float[] tagCenterY,//
			@Field("tagUserId[]") String[] tagUserId,//
			Callback<Result<ResultData>> callback);

	/**
	 * 查看用户feed
	 * 
	 * @param userId
	 *            传null看用户自己的
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.LOAD_USER_FEED)
	public Result<FeedResult> userFeed(@Field("uid") String userId, @Field("cursor") String cursor, @Field("count") Integer count);

	@FormUrlEncoded
	@POST(ServerUrlImpl.LOAD_USER_FEED)
	public void userFeed(@Field("uid") String userId, @Field("cursor") String cursor, @Field("count") Integer count, Callback<Result<FeedResult>> callback);

	/**
	 * 查看自己收到的feed
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.GET_TIME_LINE_FEED)
	public Result<FeedResult> feed(@Field("cursor") String cursor, @Field("count") Integer count);

	/**
	 * 查看自己收到的feed
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.GET_TIME_LINE_FEED)
	public void feed(@Field("cursor") String cursor, @Field("count") Integer count, Callback<Result<FeedResult>> callback);

	/**
	 * 单条Feed
	 * 
	 * @param postId
	 * @param callback
	 */
	@GET(ServerUrlImpl.FEED_DETAIL)
	public void feedDetail(@Query("postId") String postId, Callback<Result<FeedResult>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.REPORT_FEED)
	public void reportFeed(@Field("postId") String postId, @Field("reason") String reason, @Field("type") String type, Callback<Result<ResultData>> callback);

	/**
	 * 赞
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.PRAISE_USER_FEED)
	public void praiseFeed(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	/**
	 * 取消赞
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.DISLIKE_USER_FEED)
	public void dislikeFeed(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	/**
	 * 删除自己的feed
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.DELETE_USER_FEED)
	public void deleteFeed(@Field("postId") String postId, Callback<Result<ResultData>> callback);

	/**
	 * 喜欢列表
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET(ServerUrlImpl.GET_FEED_LIKE)
	public void likeFeedPersons(@Query("postId") String postId, @Query("cursor") String cursor, @Query("count") String count, Callback<Result<UserListResult>> callback);

	//
	// /**
	// * 通知列表
	// *
	// * @param cursor
	// * @param count
	// * @return
	// */
	// @GET(ServerUrlImpl.GET_NOTIFY)
	// public void getFeedNotify(@Query("cursor") String cursor, @Query("count")
	// String count, Callback<Result<NotifyResult>> callback);
	//
	// @GET(ServerUrlImpl.GET_NOTIFY_TWO)
	// public void getFeedNotifyTwo(@Query("cursor") String cursor,
	// @Query("count") String count, Callback<Result<NotifyResult>> callback);

	/**
	 * @Description: 上传当个Feed
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-28
	 */
	@Multipart
	@POST(ServerUrlImpl.UPLOAD_FEED_JPG)
	public void sendSingleFeed(@Part("image") TypedFile file, Callback<Result<ImageUploadResult>> callback);

	/**
	 * @Title: removeTag
	 * @Description: 移除圈人
	 * @param @param file
	 * @param @param callback 设定文件
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.DELETE_TAG)
	public void removeTag(@Field("postId") String postId, @Field("tagUserId") String tagUserId, Callback<ResultData> callback);

	/**
	 * @Title: removeTag
	 * @Description: 移除圈人
	 * @param @param file
	 * @param @param callback 设定文件
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.FEED_SHARE)
	public void feedShare(@Field("postId") String postId, Callback<ResultData> callback);

	/**
	 * @Title: removeTag
	 * @Description: 移除圈人
	 * @param @param file
	 * @param @param callback 设定文件
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.FEED_WEB_CALL_ALOHAAPP)
	public void feedWebCallAlohaApp(@Field("postId") String postId, @Field("shareByUserId") String shareByUserId, Callback<ResultData> callback);
}
