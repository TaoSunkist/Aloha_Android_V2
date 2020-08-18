package com.wealoha.social.impl;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import androidx.viewpager.widget.ViewPager;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 简化代码
 * @copyright wealoha.com
 * @Date:2014-10-26
 */
public class Listeners {
	public static class TextWatcherAdapter implements TextWatcher {
		public TextWatcher initTextWatcherAdapter() {
			return null;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

	public static class AnimationListeners implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

	public static class ListViewScrollMonitor implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	public static interface IChangeAlohaFrag {
		void switchMatching(int page);
	}

	public static class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

		}

	}

	public static class CusWebViewClient extends WebViewClient {

	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description 使用Fragment的Resume来控制显示的tag
	 * @copyright wealoha.com
	 * @Date:2015-1-14
	 */
	public static class MonitorMainUiBottomTagPostion {
		private int mPosition;

		public MonitorMainUiBottomTagPostion(int postion) {
			mPosition = postion;
		}

		public int getPostion() {
			return mPosition;
		}
	}
}
