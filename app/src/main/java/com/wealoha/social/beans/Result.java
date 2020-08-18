package com.wealoha.social.beans;

import java.io.Serializable;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
public class Result<T extends ResultData> implements Serializable {

	private static final long serialVersionUID = 6540499880692897496L;

	/* 成功 */
	public static final int STATUS_CODE_OK = 200;
	/* 用戶已被封禁 */
	public static final int STATUS_CODE_FORBIDEN = 401;
	/* 服務器錯誤 */
	public static final int STATUS_CODE_SERVER_ERROR = 500;
	/* 參數錯誤 */
	public static final int STATUS_CODE_PARAM_ERROR = 501;
	/** 请求太频繁 */
	public static final int STATUS_CODE_THRESHOLD_HIT = 503;

	/**
	 * @see me.cu.app.config.Constants.HttpStatus
	 */
	public int status;
	public T data;

	/**
	 * 响应是否ok
	 * 
	 * @return
	 */
	public boolean isOk() {
		// 外层没有错误(200)
		// 内存没有错误(0)
		if (status == STATUS_CODE_OK && (data == null || data.error == ResultData.ERROR_NO_ERROR)) {
			return true;
		}
		return false;
	}
}
