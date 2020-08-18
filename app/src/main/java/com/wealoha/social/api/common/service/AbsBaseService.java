package com.wealoha.social.api.common.service;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.wealoha.social.inject.Injector;

public abstract class AbsBaseService<T> implements BaseService<T> {

	protected List<T> list;
	protected final static String FIRST_PAGE = "FIRST_PAGE";
	protected String cursorId = FIRST_PAGE;
	protected final static int COUNT = 21;
	protected boolean loading;

	public AbsBaseService() {
		Injector.inject(this);
		list = new ArrayList<T>();
	}

	/**
	 * 重置数据
	 * 
	 * @return void
	 */
	public void refreshAllData() {
		cursorId = FIRST_PAGE;
		list.clear();
	}

	public void getDataList(ServiceResultCallback<T> callback, Object... args) {
		String tempCursor = cursorId;
		if (FIRST_PAGE.equals(tempCursor)) {
			tempCursor = null;
			list.clear();
		} else if (TextUtils.isEmpty(tempCursor)) {
			// list.add(getTopicReloadItem());
			callback.nomore();
			return;
		}
		getList(callback, tempCursor, args);
	}

	public abstract void getList(ServiceResultCallback<T> callback, String cursorId, Object... args);
}
