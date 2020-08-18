package com.wealoha.social.event;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-13 下午2:38:09
 */
public class NotifyCountEvent {

	private final int newNotifyCount;

	public NotifyCountEvent(int newNotifyCount) {
		super();
		this.newNotifyCount = newNotifyCount;
	}

	public int getNewNotifyCount() {
		return newNotifyCount;
	}

}
