package com.wealoha.social.beans;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:44:54
 */
public abstract class AbsNotify2 implements Notify2, Serializable {

	private static final long serialVersionUID = -5031739406933536965L;
	private final Notify2Type type;
	private final long updateTimeMillis;
	private final String notifyId; // 用来做对象相等比较
	private boolean unread;// 未读，不是final，用来重新渲染视图

	public AbsNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis) {
		super();
		// FIXME api应当返回notifyId，暂时用这个开发调试
		this.notifyId = notifyid;
		this.type = type;
		this.unread = unread;
		this.updateTimeMillis = updateTimeMillis;
	}



	public String getNotifyId() {
		return notifyId;
	}

	@Override
	public Notify2Type getType() {
		return type;
	}

	@Override
	public boolean isUnread() {
		return unread;
	}

	@Override
	public void changeReadState(boolean isread) {
		this.unread = isread;
	}

	@Override
	public long getUpdateTimeMillis() {
		return updateTimeMillis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((notifyId == null) ? 0 : notifyId.hashCode());
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
		AbsNotify2 other = (AbsNotify2) obj;
		String notifyId = getNotifyId();
		if (notifyId == null) {
			if (other.notifyId != null)
				return false;
		} else if (!notifyId.equals(other.notifyId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
