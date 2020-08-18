package com.wealoha.social.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SlideViewList extends ListView {

	private SlideView mFocusedItemView;

	public SlideViewList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SlideViewList(Context context) {
		super(context);
	}

	public SlideViewList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			int x = (int) event.getX();
			int y = (int) event.getY();
			// 我们想知道当前点击了哪一行
			int position = pointToPosition(x, y);
			if (position != INVALID_POSITION) {
				SlideView slideView = (SlideView) getItemAtPosition(position);
				mFocusedItemView = slideView;
			}
		}
		default:
			break;
		}

		// 向当前点击的view发送滑动事件请求，其实就是向SlideView发请求
		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

}
