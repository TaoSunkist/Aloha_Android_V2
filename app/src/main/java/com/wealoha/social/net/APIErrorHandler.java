package com.wealoha.social.net;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import android.content.Context;

import com.wealoha.social.utils.XL;

/**
 * 处理api请求错误
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午1:19:44
 */
public class APIErrorHandler implements ErrorHandler {

	private final String TAG = getClass().getSimpleName();

	public APIErrorHandler(Context context) {
		super();
	}

	@Override
	public Throwable handleError(RetrofitError exception) {
		XL.d(TAG, "API错误", exception);
		return exception;
	}
}
