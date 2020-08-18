package com.wealoha.social.widget;

import android.view.LayoutInflater;
import android.view.View;
import butterknife.ButterKnife;

import com.wealoha.social.adapter.feed.AbsViewHolder;

/**
 * {@link BaseListApiAdapter} 使用的ViewHolder的公用基类
 * 
 * @author javamonk
 * @createTime 2015年3月4日 上午11:24:10
 */
public abstract class BaseAdapterHolder extends AbsViewHolder {

	// AbsViewHolder.java
	private final View view;

	protected LayoutInflater inflater;

	protected BaseAdapterHolder(View view) {
		super();
		this.view = view;
		ButterKnife.inject(this, view);
	}

	public View getView() {
		return view;
	};

}
