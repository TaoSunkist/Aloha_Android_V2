package com.wealoha.social.view.custom.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.view.custom.image.FlippingImageView;

public class FlippingLoadingDialog extends BaseDialog {
	private FlippingImageView mFivIcon;
	private TextView mHtvText;
	private String mText;

	public FlippingLoadingDialog(Context context, String text) {
		super(context);
		mText = text;
		if (mText != null) {
			init(mText);
		} else {
			init();
		}
	}

	public FlippingLoadingDialog(Context context, int popupPrompt, LayoutParams layoutParams) {
		super(context);
		init(context, popupPrompt, layoutParams);
	}

	private void init(Context context, int contentResId, LayoutParams layoutParams) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.popup_prompt, null);
		setContentView(view, layoutParams);
	}

	private void init() {
		setContentView(R.layout.common_flipping_loading_diloag);
		mFivIcon = (FlippingImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (TextView) findViewById(R.id.loadingdialog_htv_text);
		mFivIcon.startAnimation();
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	private ProgressBar mProgressBar;

	private void init(String mText2) {
		setContentView(R.layout.common_flipping_loading_diloag);
		mFivIcon = (FlippingImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (TextView) findViewById(R.id.loadingdialog_htv_text);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressBar.setVisibility(View.GONE);
		mFivIcon.startAnimation();
		mHtvText.setVisibility(View.VISIBLE);
		mHtvText.setText(mText);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}
}
