package com.wealoha.social.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.wealoha.social.inject.Injector;

public class BaseLayout extends FrameLayout {

	public BaseLayout(Context context, int layoutId) {
		super(context);
		LayoutInflater.from(context).inflate(layoutId, this);
		ButterKnife.inject(this);
		Injector.inject(this);
	}

	public BaseLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BaseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void setTextorGone(String str, TextView textView) {
		if (TextUtils.isEmpty(str)) {
			textView.setVisibility(View.GONE);
			return;
		}
		textView.setVisibility(View.VISIBLE);
		textView.setText(str);
	}
}
