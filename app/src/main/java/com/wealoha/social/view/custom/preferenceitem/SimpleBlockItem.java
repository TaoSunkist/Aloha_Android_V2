package com.wealoha.social.view.custom.preferenceitem;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wealoha.social.R;

public class SimpleBlockItem implements BaseBlockItem {

	protected final String title;
	protected final String hint;
	protected final boolean hasArrowImg;
	protected final boolean hasFirstTag;

	/** item 标题资源id，onclick callback回调事件的id */
	protected final Integer itemId;
	protected BlockItemCallback itemClickCallback;

	public interface BlockItemCallback {

		/***
		 * item 的单击回调事件
		 * 
		 * @param itemIndex
		 *            item在整个界面中的位置
		 * @return void
		 */
		public void onclickCallback(int itemIndex);

		public Context getContext();
	}

	public SimpleBlockItem(BlockItemCallback itemClickCallback, int titleResource, String hint,//
			boolean hasArrowImg, boolean hasFirstTag) {
		this.hasArrowImg = hasArrowImg;
		this.hasFirstTag = hasFirstTag;
		this.itemClickCallback = itemClickCallback;
		this.hint = hint;
		this.title = itemClickCallback.getContext().getString(titleResource);
		this.itemId = titleResource;
	}

	@Override
	public void initView(LayoutInflater inflater, ViewGroup blockView, final int index) {
		View blockItem = inflater.inflate(R.layout.item_preferense_sampleview, blockView, false);
		RelativeLayout itemroot = (RelativeLayout) blockItem.findViewById(R.id.item_root);
		// itemroot.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// v.performClick();
		// // 将单击事件回调给主视图
		// if (itemClickCallback != null) {
		// itemClickCallback.onclickCallback(itemId);
		// }
		// return false;
		// }
		// });
		itemroot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (itemClickCallback != null) {
					itemClickCallback.onclickCallback(itemId);
				}
			}
		});
		TextView titleText = (TextView) blockItem.findViewById(R.id.title);
		TextView hintText = (TextView) blockItem.findViewById(R.id.hint);
		View firstSub = blockItem.findViewById(R.id.first_sub);
		ImageView arrowImg = (ImageView) blockItem.findViewById(R.id.arrow_img);

		isTextViewVisibility(hint, hintText);
		isTextViewVisibility(title, titleText);
		isViewVisibility(hasArrowImg, arrowImg);
		isViewVisibility(hasFirstTag, firstSub);

		blockView.addView(blockItem);
	}

	@Override
	public void isTextViewVisibility(String content, TextView view) {
		if (!TextUtils.isEmpty(content)) {
			view.setText(content);
		}
	}

	@Override
	public void isViewVisibility(boolean isVisibility, View view) {
		if (!isVisibility) {
			view.setVisibility(View.GONE);
		}
	}
}
