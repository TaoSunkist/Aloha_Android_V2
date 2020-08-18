package com.wealoha.social.beans.common;

import retrofit.Callback;
import retrofit.http.GET;

import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-12 上午10:29:05
 */
public interface CountService {

	/**
	 * 计数
	 * 
	 * @param callback
	 */
	@GET(ServerUrlImpl.COUNT)
	public void count(Callback<Result<CountData>> callback);
}
