package com.wealoha.social.event;

public class DeleteCommentEvent {
	private final String sessionId;

	public DeleteCommentEvent(String mId) {
		sessionId = mId;
	}

	public String getCommentId() {
		return sessionId;
	}
}
