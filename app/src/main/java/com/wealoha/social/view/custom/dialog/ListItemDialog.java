package com.wealoha.social.view.custom.dialog;

import javax.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;

public class ListItemDialog implements OnClickListener {

	@Inject
	ContextUtil contextUtil;
	private Activity mAct;
	private ViewGroup mContainer;
	private int itemHeight;
	private int itemLeftMargin;
	private float itemTitleTxtSize;
	private Dialog mDialog;
	private Builder mBuilder;
	private TextView mTitleTv;
	private boolean ishaveTitle;

	private ListItemCallback mListItemCallback;

	public ListItemDialog(Activity act, ViewGroup rootView) {
		Injector.inject(this);
		mAct = act;
		mContainer = (ViewGroup) LayoutInflater.from(mAct).inflate(R.layout.dialog_list_item, rootView, false);
		itemHeight = UiUtils.dip2px(mAct, 50);
		itemLeftMargin = UiUtils.dip2px(mAct, 15);
		itemTitleTxtSize = 16;
		findViews();
	}

	private void findViews() {
		mTitleTv = (TextView) mContainer.findViewById(R.id.popup_title);
		FontUtil.setRegulartypeFace(mAct, mTitleTv);
	}

	public void showListItemPopup(ListItemCallback listItemCallback, String title, int... listItemType) {
		// mContainer.removeAllViews();
		// mContainer.removeViews(1, mContainer.getChildCount() - 1);
		for (int index = 0; index < listItemType.length; index++) {
			createItem(mContainer, listItemType[index]);
		}
		if (TextUtils.isEmpty(title)) {
			mTitleTv.setVisibility(View.GONE);
		} else {
			ishaveTitle = true;
			mTitleTv.setVisibility(View.VISIBLE);
			mTitleTv.setText(title);
		}
		mListItemCallback = listItemCallback;
		show();
	}

	private void show() {
		// if (mDialog == null) {
		if (mAct == null || mAct.isFinishing()) {
			return;
		}
		mBuilder = new AlertDialog.Builder(mAct);

		mDialog = mBuilder.setView(mContainer).create();
		// } else {
		// mDialog.setContentView(mContainer);
		// }

		mDialog.show();
	}

	private void createItem(ViewGroup container, int listitemtype) {
		int viewTitle = 0;
		switch (listitemtype) {
		case ListItemType.DELETE_FEED_ITEM:
			viewTitle = R.string.delete;
			break;
		case ListItemType.DELETE_TAG_ITEM:
			viewTitle = R.string.remove_tag_dialog;
			break;
		case ListItemType.REPORT_FEED_ITEM:
			viewTitle = R.string.report_inappropriate_content;
			break;
		case ListItemType.REMOVE_OLD_VENUES:
			viewTitle = R.string.remove_old_venues;
			break;
		case ListItemType.USER_NEW_VENUES:
			viewTitle = R.string.user_new_venues;
			break;
		case ListItemType.DELETE:
			viewTitle = R.string.delete;
			break;
		case ListItemType.LOGOUT:
			viewTitle = R.string.logout;
			// 登出dialog 要做样式的特殊处理
			mTitleTv.setTextSize(12);
			mTitleTv.setTextColor(mAct.getResources().getColor(R.color.gray_text));
			break;
		case ListItemType.REPORT_USER:
			viewTitle = R.string.report;
			break;
		case ListItemType.CAMERA:
			viewTitle = R.string.camera_capture;
			break;
		case ListItemType.CHOSE_PHOTOS:
			viewTitle = R.string.camera_select;
			break;
		case ListItemType.SHARE_POST:
			viewTitle = R.string.share_post;
			break;
		case ListItemType.STOP:
			return;
		default:
			return;
		}
		createView(container, listitemtype, viewTitle);
	}

	private void createView(ViewGroup container, int viewid, int viewTitle) {
		View lineView = new View(mAct);
		lineView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, 1));
		lineView.setBackgroundColor(mAct.getResources().getColor(R.color.line));

		TextView itemTextView = new TextView(mAct);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, itemHeight);
		FontUtil.setRegulartypeFace(mAct, itemTextView);
		itemTextView.setPadding(itemLeftMargin, 0, 0, 0);
		itemTextView.setLayoutParams(params);
		itemTextView.setBackgroundResource(R.drawable.dialog_click_selector);
		itemTextView.setClickable(true);
		itemTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		
		// FIXME 结构被破坏 重新设计
		if (viewid == ListItemType.LOGOUT) {
			itemTextView.setTextColor(mAct.getResources().getColor(R.color.light_red));
			itemTextView.setTextSize(18);
		} else {
			itemTextView.setTextColor(mAct.getResources().getColor(R.color.black_text));
			itemTextView.setTextSize(itemTitleTxtSize);
		}

		itemTextView.setOnClickListener(this);

		itemTextView.setText(viewTitle);
		itemTextView.setId(viewid);
		XL.i("REMOVE_FROM_BLOCK", "POSTION:" + container.getChildCount());
		if (container.getChildCount() == 1 && !ishaveTitle) {
			container.addView(itemTextView);
		} else {
			container.addView(lineView);
			container.addView(itemTextView);
		}
	}

	@Override
	public void onClick(View v) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.hide();
		}
		if (mListItemCallback != null) {
			mListItemCallback.itemCallback(v.getId());
		}
	}

	public interface ListItemType {

		public static final int STOP = 0x000000;
		public static final int REPORT_FEED_ITEM = 0x000001;
		public static final int DELETE_FEED_ITEM = 0x000002;
		public static final int DELETE_TAG_ITEM = 0x000003;

		// 是否在分享feed时分享地理位置
		public static final int REMOVE_OLD_VENUES = 0x000004;
		public static final int USER_NEW_VENUES = 0x000005;

		public static final int LOGOUT = 0x000006;
		public static final int DELETE = 0x000007;
		public static final int REPORT_USER = 0x000008;
		public static final int CAMERA = 0x000009;
		public static final int CHOSE_PHOTOS = 0x000010;
		public static final int SHARE_POST = 0x000011;
	}

	public interface ListItemCallback {

		public void itemCallback(int listItemType);
	}

}
