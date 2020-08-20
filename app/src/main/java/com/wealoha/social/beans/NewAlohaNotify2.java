package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.List;

import com.wealoha.social.api.user.bean.User;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 下午12:23:34
 */
public class NewAlohaNotify2 extends AbsNotify2 implements Serializable {
	private int count;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5313343546495977449L;
	private final List<User> users;

	public NewAlohaNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis, int count, List<User> users) {
		super(type, unread, notifyid, updateTimeMillis);
		this.users = users;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<User> getUsers() {
		return users;
	}
}
