package com.wealoha.social.event;

public class ClearFeedNotifyEvent {

	public boolean isClear;

	public ClearFeedNotifyEvent(boolean clear) {
		isClear = clear;
	}

	public boolean isClear() {
		return isClear;
	}

	public void setClear(boolean isClear) {
		this.isClear = isClear;
	}

}
