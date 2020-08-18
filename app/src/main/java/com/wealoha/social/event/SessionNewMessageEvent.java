package com.wealoha.social.event;

/**
 * 有新消息到达了
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-13 上午11:58:15
 */
public class SessionNewMessageEvent {

	public final String sessionId;
	public final String messageId;

	public SessionNewMessageEvent(String sessionId, String messageId) {
		super();
		this.sessionId = sessionId;
		this.messageId = messageId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getMessageId() {
		return messageId;
	}

}
