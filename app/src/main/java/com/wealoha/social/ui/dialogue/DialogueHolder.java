package com.wealoha.social.ui.dialogue;

import com.wealoha.social.beans.User;

/**
 * @author sunkist
 * @description 会话页面的数据持有者
 */
public class DialogueHolder {

	private String sessionId;
	private User toUser;
	private boolean forceBackToSessionList;
	private boolean showMatchHint;
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public boolean isForceBackToSessionList() {
		return forceBackToSessionList;
	}

	public void setForceBackToSessionList(boolean forceBackToSessionList) {
		this.forceBackToSessionList = forceBackToSessionList;
	}

	
	public boolean isShowMatchHint() {
		return showMatchHint;
	}

	
	public void setShowMatchHint(boolean showMatchHint) {
		this.showMatchHint = showMatchHint;
	}
	
}
