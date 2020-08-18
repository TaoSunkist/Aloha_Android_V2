package com.wealoha.social.beans.message;

import java.io.Serializable;

/**
 * Created by walker on 14-4-15.
 */
public abstract class Message implements Serializable {

	private static final long serialVersionUID = -89575831597711736L;
	public String id;
	public String type;
	public String toUserId;
	public boolean mine;
	public String state;
	public long createTimeMillis;

	/** 发送失败1、发送成功-1、正在发送0 */
	public int smsStatus = -1;

	// 以下是本地状态
	/** 本地临时缓存的消息 */
	public boolean isLocal;
	public boolean sending;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/** 是否发送成功 */
	public boolean sendFail;
	/** 是否显示时间戳 */
	public boolean showTimestamp;

	@Override
	public String toString() {
		return "Message [id=" + id + ", type=" + type + ", toUserId=" + toUserId + ", mine=" + mine + ", state=" + state + ", createTimeMillis=" + createTimeMillis + ", smsStatus=" + smsStatus + ", isLocal=" + isLocal + ", sending=" + sending + ", sendFail=" + sendFail + ", showTimestamp=" + showTimestamp + "]";
	}

	
	

}
