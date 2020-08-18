package com.wealoha.social.view.custom.preferenceitem;

import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wealoha.social.R;

public class PreferenceBlock implements BaseBlock {

	private ViewGroup mBlockViewRoot;

	public PreferenceBlock(LayoutInflater inflater, ViewGroup parentView, List<BaseBlockItem> blockItems, String title) {
		ViewGroup blockView = initBlockViewGroup(inflater, parentView, title);
		for (int i = 0; i < blockItems.size(); i++) {
			BaseBlockItem blockItem = blockItems.get(i);
			// if (blockItem instanceof SimpleBlockItem) {
			// blockItem.initView(inflater, blockView, i);
			// } else if (blockItem instanceof ProfileBlockItem) {
			// }
			blockItem.initView(inflater, blockView, i);
		}
		parentView.addView(mBlockViewRoot);
	}

	/**
	 * 初始化block 容器
	 * 
	 * @param frag
	 * @param parentView
	 *            fragment根view
	 * @return View
	 */
	public ViewGroup initBlockViewGroup(LayoutInflater inflater, ViewGroup parentView, String title) {
		mBlockViewRoot = (ViewGroup) inflater.inflate(R.layout.item_preferense_block, parentView, false);
		ViewGroup blockView = (ViewGroup) mBlockViewRoot.findViewById(R.id.item_container);
		TextView titleText = (TextView) mBlockViewRoot.findViewById(R.id.title);
		if (!TextUtils.isEmpty(title)) {
			titleText.setText(title);
		} else {
			titleText.setVisibility(View.GONE);
		}
		return blockView;
	}
}
