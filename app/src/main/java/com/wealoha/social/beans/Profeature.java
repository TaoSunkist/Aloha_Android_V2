package com.wealoha.social.beans;

import android.content.Context;

public class Profeature {

	private String title;
	private String itemTitle;
	private String itemContent;
	private int headerResId;

	public Profeature(String title, String itemTitle, String itemContent, int headerResId) {
		super();
		this.title = title;
		this.itemTitle = itemTitle;
		this.itemContent = itemContent;
		this.headerResId = headerResId;
	}

	public Profeature(Context context, int itemTitleId, int itemContentId, int stateid, int headerResId) {
		title = getString(context, itemTitleId);
		itemTitle = getString(context, itemContentId);
		itemContent = getString(context, stateid);
		this.headerResId = headerResId;
	}

	private String getString(Context context, int stringId) {
		if (stringId == 0) {
			return null;
		} else {
			return context.getResources().getString(stringId);
		}
	}

	public String getTitle() {
		return title;
	}

	public String getExplain() {
		return itemTitle;
	}

	public String getState() {
		return itemContent;
	}

	public int getHeaderResId() {
		return headerResId;
	}

}
