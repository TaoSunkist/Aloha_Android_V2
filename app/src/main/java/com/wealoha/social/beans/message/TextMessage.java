package com.wealoha.social.beans.message;


/**
 * Created by walker on 14-4-27.
 */
public class TextMessage extends Message {

	private static final long serialVersionUID = -567229340829512956L;
	public static final String TYPE = "inboxMessageText";
	public String text;

	public TextMessage() {
		type = TYPE;
	}

	@Override
	public String toString() {
		return text;
	}
}
