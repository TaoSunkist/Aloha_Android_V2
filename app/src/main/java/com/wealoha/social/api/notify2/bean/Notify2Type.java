package com.wealoha.social.api.notify2.bean;

import java.util.HashMap;
import java.util.Map;

import com.wealoha.social.widget.MultiListViewType;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:45:30
 */
public enum Notify2Type implements MultiListViewType {
	PostLike("PostLike", 0), //
	NewAloha("NewAloha", 1), //
	PostComment("PostComment", 2), //
	PostTag("PostTag", 3), //
	PostCommentReplyOnMyPost("PostCommentReplyOnMyPost", 4), //
	PostCommentReplyOnOthersPost("PostCommentReplyOnOthersPost", 5), //
	;

	private final String value;
	private final int mViewType;
	private static final Map<String, Notify2Type> valuesMap;

	static {
		valuesMap = new HashMap<String, Notify2Type>();
		for (Notify2Type t : values()) {
			valuesMap.put(t.getValue(), t);
		}
	}

	private Notify2Type(String value, int viewType) {
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

	public static Notify2Type fromValue(String value) {
		return valuesMap.get(value);
	}
}
