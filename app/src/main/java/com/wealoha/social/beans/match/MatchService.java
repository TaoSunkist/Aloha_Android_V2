package com.wealoha.social.beans.match;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.region.RegionResult;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-13 下午3:40:31
 */
public interface MatchService {

	/**
	 * 返回数据
	 */
	@GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
	public Result<MatchData> findRandom(@Query("latitude") Double latitude, @Query("longitude") Double longitude);

	/**
	 * 返回数据
	 */
	@GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
	public void findRandom(@Query("latitude") Double latitude, @Query("longitude") Double longitude, Callback<Result<MatchData>> callback);

	/**
	 * 重置配额，并且返回数据
	 * 
	 * @param resetQuota
	 */
	@GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
	public Result<MatchData> findWithResetQuota(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("resetQuota") boolean resetQuota);

	/**
	 * Aloha一个用户
	 * 
	 * @param userId
	 * @return
	 */
	// @POST(ServerUrlImpl.ALOHA_LIKE)
	// @FormUrlEncoded
	// public Result<ResultData> like(@Field("userId") String userId);

	/**
	 * Aloha一个用户
	 * 
	 * @param userId
	 * @return
	 */
	@POST(ServerUrlImpl.ALOHA_LIKE)
	@FormUrlEncoded
	public void like(@Field("userId") String userId, @Field("refer") String refer, Callback<Result<ResultData>> callback);

	@POST(ServerUrlImpl.ALOHA_LIKE)
	@FormUrlEncoded
	public Result<ResultData> like(@Field("userId") String userId, @Field("refer") String refer);

	/**
	 * Nope一个用户
	 * 
	 * @param userId
	 * @return
	 */
	@POST(ServerUrlImpl.ALOHA_DISLIKE)
	@FormUrlEncoded
	public Result<ResultData> dislike(@Field("userId") String userId);

	/**
	 * @Description:
	 * @param filterRegion
	 *            regions里选择的key或者空(可以不传，产品叫做『智能推荐』)
	 * @param filterAgeRangeStart
	 *            年龄区间，无不限制不传
	 * @param filterAgeRangeEnd
	 *            年龄区间，无不限制不传
	 * 
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月9日
	 */
	@POST("/v1/user/setting/match")
	@FormUrlEncoded
	public void reqRegions(@Field("filterRegion") String filterRegion,//
			@Field("filterAgeRangeStart") int filterAgeRangeStart,//
			@Field("filterAgeRangeEnd") int filterAgeRangeEnd, //
			Callback<Result<RegionResult>> callback);
}
