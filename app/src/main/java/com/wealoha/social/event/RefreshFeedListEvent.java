package com.wealoha.social.event;

public class RefreshFeedListEvent {
	private int mPosition;

	public RefreshFeedListEvent(int position) {
		mPosition = position;
	}

	public int getPostion() {
		return mPosition;
	}
}
