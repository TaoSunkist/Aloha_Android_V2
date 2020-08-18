package com.wealoha.social.event;

public class ControlUserEvent {
	private int mPosition;

	public ControlUserEvent(int mposition) {
		mPosition = mposition;
	}

	public int getPostion() {
		return mPosition;
	}
}
