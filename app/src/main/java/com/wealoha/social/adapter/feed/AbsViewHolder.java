package com.wealoha.social.adapter.feed;

import android.view.View;

//BaseFeedHolder
public abstract class AbsViewHolder {

	private boolean newHolder;

	protected AbsViewHolder() {
		newHolder = true;
	}

	public abstract View getView();

	/**
	 * 是否新建的
	 * 
	 * @return
	 */
	public boolean isNewHolder() {
		return newHolder;
	}

	/**
	 * 设置是否为新建的<br/>
	 * 
	 * 注意：只应当由框架层调用
	 * 
	 * @param newHolder
	 */
	public void setNewHolder(boolean newHolder) {
		this.newHolder = newHolder;
	}
}
