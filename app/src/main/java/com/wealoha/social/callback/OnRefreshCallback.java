package com.wealoha.social.callback;

public interface OnRefreshCallback<T> {
	public void success(T t);

	public void failure();
}
