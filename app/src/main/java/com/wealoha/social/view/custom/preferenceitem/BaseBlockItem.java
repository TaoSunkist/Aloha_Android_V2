package com.wealoha.social.view.custom.preferenceitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public interface BaseBlockItem {

	/***
	 * 初始化block item
	 * 
	 * @param inflater
	 * @param blockView
	 *            item的父容器
	 * @param index
	 *            item 在整个界面中的顺序，为了在界面中回调点击事件而做的标记
	 */
	public void initView(LayoutInflater inflater, ViewGroup blockView, int index);

	/***
	 * 是否显示文字信息
	 * 
	 * @param content
	 *            文字信息
	 * @param view
	 *            文字信息控件
	 */
	public void isTextViewVisibility(String content, TextView view);

	/***
	 * 这个控件是否被配置在当前item中
	 * 
	 * @param isVisibility
	 *            是否被配置
	 * @param view
	 *            当前控件
	 */
	public void isViewVisibility(boolean isVisibility, View view);
}
