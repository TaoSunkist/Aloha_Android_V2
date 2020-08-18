package com.wealoha.social.event;

import com.wealoha.social.beans.message.Message;

public class DialogueLastNewMsg {
	private Message mMessage;

	public DialogueLastNewMsg(Message message) {
		mMessage = message;
	}

	public Message getMessage() {
		return mMessage;
	}

}
