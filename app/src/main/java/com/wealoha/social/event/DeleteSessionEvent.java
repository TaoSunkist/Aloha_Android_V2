package com.wealoha.social.event;

public class DeleteSessionEvent {
	private final String sessionId;

	public DeleteSessionEvent(String mId) {
		sessionId = mId;
	}

	public String getSessionId() {
		return sessionId;
	}
}
