package com.wealoha.social.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by walker on 14-4-5.
 */
public class UiUtils {

	private static int SCREENT_WIDTH;
	private static int SCREENT_HEIGHT;

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static void showKeyBoard(Activity activity, View view, int flags) {
		((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, flags);
	}

	public static void hideKeyBoard(Activity activity) {
		if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
			InputMethodManager manager = (InputMethodManager) activity.getSystemService((Context.INPUT_METHOD_SERVICE));
			manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static Point getScreenPoint(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static int getScreenWidth(Context context) {
		if (SCREENT_WIDTH == 0) {
			SCREENT_WIDTH = getScreenPoint(context).x;
		}
		return SCREENT_WIDTH;
	}

	public static int getScreenHeight(Context context) {
		if (SCREENT_HEIGHT == 0) {
			SCREENT_HEIGHT = getScreenPoint(context).y;
		}
		return SCREENT_HEIGHT;
	}

	public static int getWindowHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

		// 窗口高度
		int screenHeight = dm.heightPixels;

		return screenHeight;
	}

}
