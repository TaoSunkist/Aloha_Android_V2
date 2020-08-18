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
 * @date 2014-11-20 下午5:51:11
 */
public interface ConstantsService {

	@GET(ServerUrlImpl.CONSTANTS)
	public void get(Callback<Result<ConstantsData>> callback);

	/**
	 * API可用的入口
	 * 
	 * @return
	 */
	@GET(ServerUrlImpl.CONSTANTS_API_ENDPOINT)
	public Result<ApiEndpointData> apiEndpoing();

}
