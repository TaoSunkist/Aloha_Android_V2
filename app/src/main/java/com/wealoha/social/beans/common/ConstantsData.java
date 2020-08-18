package com.wealoha.social.beans.common;

import java.util.Map;

import com.wealoha.social.beans.ResultData;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午5:57:38
 */
public class ConstantsData extends ResultData {

	public static final String STARTUP_IMAGE_720P = "Android720p";
	public static final String STARTUP_IMAGE_1080P = "Android1080p";

	public boolean hasUpdateVersion;
	public String officialUserId;
	public String updateDetails;
	public int startupImageShowIntervalMinutes;

	/**
	 * 开机画面
	 */
	public Map<String, String> startupImageMap;
}
