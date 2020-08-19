package com.wealoha.social.beans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

public class ShareApp {

	private final int id;
	private int iconId;
	private String title;
	private Drawable icon;
	private final OnClickListener clickListener;

	public ShareApp(Context context, int iconId, int titleId, OnClickListener clickListener) {
		this.iconId = iconId;
		id = iconId;
		icon = context.getResources().getDrawable(iconId);
		title = context.getString(titleId);
		this.clickListener = clickListener;
	}

	public int getIconId() {
		return iconId;
	}

	public String getTitle() {
		return title;
	}

	public Drawable getIcon() {
		return icon;
	}

	public int getId() {
		return id;
	}

	public OnClickListener getClickListener() {
		return clickListener;
	}

}
