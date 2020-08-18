package com.wealoha.social.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wealoha.social.R;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description Toast提示工具类
 * @copyright wealoha.com
 * @Date:2014-10-26
 */
public class ToastUtil {

	private static Toast toast = null;

	/**
	 * 长时间
	 * 
	 * @param context
	 * @param message
	 */
	public static void longToast(Context context, String message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}
		toast.show();
	}

	public static void longToast(Context context, int id) {
		if (toast == null) {
			toast = Toast.makeText(context, context.getResources().getString(id), Toast.LENGTH_LONG);
		} else {
			cancelToast();
			toast = Toast.makeText(context, context.getResources().getString(id), Toast.LENGTH_LONG);
		}
		toast.show();
	}

	/**
	 * 长时间 控制位置
	 * 
	 * @param context
	 * @param message
	 */
	public static void longToast(Context context, String message, int xOffset, int yOffset) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}
		toast.setGravity(Gravity.CENTER, xOffset, yOffset);
		toast.show();
	}

	/**
	 * 长时间 中间位置显示
	 * 
	 * @param context
	 * @param message
	 */
	public static void longToastCenter(Context context, String message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}
		toast.setGravity(Gravity.CENTER, new Toast(context).getXOffset() / 2, new Toast(context).getYOffset() / 2);
		toast.show();
	}

	/**
	 * 短时间
	 * 
	 * @param context
	 * @param message
	 */
	public static void shortToast(Context context, String message) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	/**
	 * 短时间
	 * 
	 * @param context
	 * @param message
	 */
	public static void shortToast(Context context, int resId) {
		if (toast == null) {
			toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		} else {
			cancelToast();
			toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	/**
	 * 短时间 控制位置
	 * 
	 * @param context
	 * @param message
	 */
	public static void shortToast(Context context, String message, int xOffset, int yOffset) {
		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}
		toast.setGravity(Gravity.CENTER, xOffset, yOffset);
		toast.show();
	}

	/**
	 * 短时间 中间位置显示
	 * 
	 * @param context
	 * @param message
	 */
	public static void shortToastCenter(Context context, String message) {

		if (toast == null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			cancelToast();
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}
		toast.setGravity(Gravity.CENTER, new Toast(context).getXOffset() / 2, new Toast(context).getYOffset() / 2);
		toast.show();
	}

	/**
	 * 显示自定义Toast提示(来自res) *
	 */
	public static void showCustomToast(Context context, int resId) {
		View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast_common, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(context.getString(resId));
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	public static void cancelToast() {
		if (toast != null)
			toast.cancel();
	}

}
