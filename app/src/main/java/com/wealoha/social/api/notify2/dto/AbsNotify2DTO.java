package com.wealoha.social.api.notify2.dto;

/**
 * Notify2的数据传输对象，和API保持一致
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:24:44
 */
public class AbsNotify2DTO {

	public String notifyId;
	public String type;
	public boolean unread;
	public long updateTimeMillis;
	public int count;
}
