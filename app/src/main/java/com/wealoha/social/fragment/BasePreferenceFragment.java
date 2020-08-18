package com.wealoha.social.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wealoha.social.R;
import com.wealoha.social.view.custom.preferenceitem.BaseBlockItem;
import com.wealoha.social.view.custom.preferenceitem.PreferenceBlock;
import com.wealoha.social.view.custom.preferenceitem.SimpleBlockItem.BlockItemCallback;

public abstract class BasePreferenceFragment extends Fragment implements BlockItemCallback {

	protected View rootView;
	protected ViewGroup blockRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,//
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.frag_config_2, container, false);
		blockRootView = (ViewGroup) rootView.findViewById(R.id.item_container);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initBlockItems();
	}

	/**
	 * 装填偏好设定类型的界面中的块元素 集合，这个集合决定了 界面要显示多少块元素，集合内的对象决定了块元素内的视图种类
	 * 
	 * @param
	 * @return void
	 */
	public abstract void initBlockItems();

	/***
	 * 初始化偏好设定类型的界面中的块元素， 并将组装好的块元素集合填充到块元素中，在{@link #initBlockItems()} 中调用
	 * 
	 * @param blockItems
	 * @return void
	 */
	public void initBlocks(List<BaseBlockItem> blockItems, String title) {
		new PreferenceBlock(getActivity().getLayoutInflater(), blockRootView, blockItems, title);
	};

	public void initBlocks(List<BaseBlockItem> blockItems) {
		initBlocks(blockItems, null);
	};

	/***
	 * 装填block 中的item
	 * 
	 * @param title
	 *            block的标题
	 * @param blockItems
	 *            block的item
	 */
	public void initBlockView(String title, BaseBlockItem... blockItems) {
		List<BaseBlockItem> blockItemList = new ArrayList<BaseBlockItem>(blockItems.length);
		for (BaseBlockItem blockItem : blockItems) {
			blockItemList.add(blockItem);
		}
		new PreferenceBlock(getActivity().getLayoutInflater(), blockRootView, blockItemList, title);
	};

	@Override
	public Context getContext() {
		return getActivity();
	}
}
