package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.PromotionGetData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.GET

/**
 * @author javamonk
 * @createTime 14-10-16 PM12:02
 */
interface UserPromotionService {
    /**
     * 获取邀请状态
     *
     * @return
     */
    @GET(ServerUrlImpl.GET_USER_PROMOTION)
    fun get(): Result<PromotionGetData?>?

    /**
     * 获取邀请状态
     *
     * @return
     */
    @GET(ServerUrlImpl.GET_USER_PROMOTION)
    operator fun get(callback: Callback<Result<PromotionGetData?>?>?) // /**
    // * 新注册用户，提交邀请码使用
    // *
    // * @param code
    // * @return
    // */
    // @FormUrlEncoded
    // @POST(GlobalConstants.ServerUrl.LOAD_USER_FEED)
    // public Result<PromotionSetData> submit(@Field("code") String code);
}