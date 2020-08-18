package com.wealoha.social.utils;

import android.view.View;
import android.view.View.OnClickListener;

/***
 * @author super 单击和双击事件
 */
public abstract class OnClickListenerUtil implements OnClickListener {

	private long firstClickTime;
	private long clickInterval;
	private boolean doubleClick;
	private View view;
	private Runnable clickRunnable = new Runnable() {

		@Override
		public void run() {
			if (!doubleClick) {
				OnClickEvent(view);
				firstClickTime = 0;
			}
			doubleClick = false;
		}
	};

	/***
	 * @param 区分单击和双击事件的时间间隔
	 */
	public OnClickListenerUtil(long clickInterval) {
		if (clickInterval < 200) {
			clickInterval = 200;
		}
		this.clickInterval = clickInterval;
	}

	@Override
	public void onClick(View v) {
		if (view == null) {
			view = v;
		}

		if (System.currentTimeMillis() - firstClickTime <= clickInterval) {
			firstClickTime = 0;
			doubleClick = true;
			OnDoubleClickEvent(v);
		} else {
			firstClickTime = System.currentTimeMillis();
			v.removeCallbacks(clickRunnable);
			v.postDelayed(clickRunnable, clickInterval);
		}
	}

	public abstract void OnDoubleClickEvent(View v);

	public abstract void OnClickEvent(View v);
}
