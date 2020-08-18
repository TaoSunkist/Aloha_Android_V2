package com.wealoha.social.beans.search;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

public interface FindYouService {

	/**
	 * 通知列表
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.FIND_YOU)
	public void findYou(@Field("keyword") String keyword, @Field("count") Integer count, Callback<Result<FindYouResult>> callback);

	/**
	 * 默认圈人列表
	 * 
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET(ServerUrlImpl.TAG_SUGGEST)
	public void defaultTagUsers(Callback<Result<FindYouResult>> callback);
}
