package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.MatchData
import com.wealoha.social.beans.region.RegionResult
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-13 下午3:40:31
 */
interface MatchService {
    /**
     * 返回数据
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findRandom(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Result<MatchData?>?

    /**
     * 返回数据
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findRandom(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        callback: Callback<Result<MatchData?>?>?
    )

    /**
     * 重置配额，并且返回数据
     *
     * @param resetQuota
     */
    @GET(ServerUrlImpl.LOAD_OTHER_USER_DATA)
    fun findWithResetQuota(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("resetQuota") resetQuota: Boolean
    ): Result<MatchData?>?
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
    fun like(
        @Field("userId") userId: String?,
        @Field("refer") refer: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    @POST(ServerUrlImpl.ALOHA_LIKE)
    @FormUrlEncoded
    fun like(
        @Field("userId") userId: String?,
        @Field("refer") refer: String?
    ): Result<ResultData?>?

    /**
     * Nope一个用户
     *
     * @param userId
     * @return
     */
    @POST(ServerUrlImpl.ALOHA_DISLIKE)
    @FormUrlEncoded
    fun dislike(@Field("userId") userId: String?): Result<ResultData?>?

    /**
     * @Description:
     * @param filterRegion
     * regions里选择的key或者空(可以不传，产品叫做『智能推荐』)
     * @param filterAgeRangeStart
     * 年龄区间，无不限制不传
     * @param filterAgeRangeEnd
     * 年龄区间，无不限制不传
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
    fun reqRegions(
        @Field("filterRegion") filterRegion: String?,  //
        @Field("filterAgeRangeStart") filterAgeRangeStart: Int,  //
        @Field("filterAgeRangeEnd") filterAgeRangeEnd: Int,  //
        callback: Callback<Result<RegionResult?>?>?
    )
}