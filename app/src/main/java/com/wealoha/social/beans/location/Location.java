package com.wealoha.social.beans.location;

import java.io.Serializable;

import android.text.TextUtils;

public class Location implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -3591352045359882813L;
	public String address;
	public String name;
	public String id;
	public Double latitude;
	public Double longitude;

	/**
	 * @Title: isEmpty
	 * @Description: 地理位置信息是否为空
	 */
	public boolean isEmpty() {
		if (TextUtils.isEmpty(id) || latitude == null || longitude == null) {
			return true;
		}
		return false;
	}
}
