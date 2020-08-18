package com.wealoha.social.api.feed.bean;

import java.util.HashMap;
import java.util.Map;

import com.wealoha.social.widget.MultiListViewType;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:45:30
 */
public enum FeedType implements MultiListViewType {
	VideoPost("VideoPost", 0), //
	ImagePost("ImagePost", 1), //
	;

	private final String value;
	private final int mViewType;
	private static final Map<String, FeedType> valuesMap;

	static {
		valuesMap = new HashMap<String, FeedType>();
		for (FeedType t : values()) {
			valuesMap.put(t.getValue(), t);
		}
	}

	private FeedType(String value, int viewType) {
		this.value = value;
		mViewType = viewType;
	}

	@Override
	public int getViewType() {
		return mViewType;
	}

	public String getValue() {
		return value;
	}

	public static FeedType fromValue(String value) {
		return valuesMap.get(value);
	}
}
