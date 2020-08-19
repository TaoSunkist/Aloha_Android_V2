package com.wealoha.social.api;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

import com.wealoha.social.beans.PrivacyData;
import com.wealoha.social.beans.Result;

/**
 * @author:sunkist
 * @description:
 * @Date:2015年8月4日
 */
public interface UserSettingPrivacyAPI {
    //
    // /v1/user/setting/privacy

    /**
     * @param callback
     * @author: sunkist
     * @description: matchExcludeDistanceKm
     * 不要在多近的范围内推荐我。空: 没有设置；大于200，不推荐
     * @date:2015年8月4日
     */
    @GET("/v1/user/setting/privacy")
    public void getPrivacy(Callback<Result<PrivacyData>> callback);

    /**
     * @param matchExcludeDistanceKm 1-1000有效，不传抹掉；大于200隐身
     * @param callback
     * @author: sunkist
     * @description:
     * @date:2015年8月4日
     */
    @POST("/v1/user/setting/privacy")
    @FormUrlEncoded
    public void setPrivacy(@Field("matchExcludeDistanceKm") Integer matchExcludeDistanceKm, Callback<Result<PrivacyData>> callback);
}
