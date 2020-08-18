package com.wealoha.social.event.push;

/**
 * 有新Push通知到达
 * 
 * @author javamonk
 * @createTime 2015年3月5日 下午8:55:40
 */
public class NewNotifyEvent {
	public int notifyCount;

	public NewNotifyEvent() {

	}

	public NewNotifyEvent(int notifyCount) {
		this.notifyCount = notifyCount;
	}
}
