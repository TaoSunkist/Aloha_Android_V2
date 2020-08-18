package com.wealoha.social.api.notify2.bean;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:39:02
 */
public interface Notify2 {

	/**
	 * feed被喜欢的通知.
	 * 
	 * @deprecated
	 */
	int POST_LIKE_VIEW_TYPE = 0;
	/**
	 * 人气的通知
	 * 
	 * @deprecated
	 */
	int NEW_ALOHA_VIEW_TYPE = 1;
	/**
	 * Feed评论的通知
	 * 
	 * @deprecated
	 */
	int POST_COMMENT_TAG_TYPE = 2;
	/**
	 * 圈人的通知
	 * 
	 * @deprecated
	 */
	int POST_TAG_VIEW_TYPE = 3;

	public Notify2Type getType();

	public boolean isUnread();

	public void changeReadState(boolean isread);

	public long getUpdateTimeMillis();
}
