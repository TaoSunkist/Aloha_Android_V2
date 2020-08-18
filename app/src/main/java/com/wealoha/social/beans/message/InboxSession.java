package com.wealoha.social.beans.message;

import java.io.Serializable;

import com.wealoha.social.beans.User;

/**
 * Created by walker on 14-4-14.
 */
public class InboxSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3234191364565823058L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InboxSession other = (InboxSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static final String TAG = InboxSession.class.getSimpleName();
	public String id;
	public String type;
	public User user;
	public long updateTimeMillis;
	public int unread;
	public boolean showMatchHint;
}
