package com.wealoha.social.widget;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class NoUnderlineClickableSpan extends ClickableSpan {

	// private ClickableSpanClickListener clickListener;
	//
	// public NoUnderlineClickableSpan(ClickableSpanClickListener clickListener) {
	// this.clickListener = clickListener;
	// }

	@Override
	public void updateDrawState(TextPaint tp) {
		tp.setUnderlineText(false);
	}

	// @Override
	// public void onClick(View widget) {
	// clickListener.onClick(widget);
	// }

	public interface ClickableSpanClickListener {

		void onClick(View widget);
	}
}
