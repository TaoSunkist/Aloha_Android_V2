package com.wealoha.social.beans.message;

import com.wealoha.social.beans.Image;

/**
 * Created by walker on 14-4-27.
 */
public class ImageMessage extends Message {

	private static final long serialVersionUID = 1025221798212317198L;
	public static final String TYPE = "inboxMessageImage";

	public Image image;

	public ImageMessage() {
		type = TYPE;
	}
}
