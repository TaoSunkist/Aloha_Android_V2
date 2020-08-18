package com.wealoha.social.utils;

import android.util.Log;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 记录log 请少使用error类型的日志输出
 * @copyright wealoha.com
 * @Date:2014-10-24
 */
public class XL {

	private static final boolean LOG = true;// true false时则关闭log
	// failfast log
	private static final String NULL_STR = "msg is null!";

	public static void d(String tag, String msg) {
		if (LOG)
			Log.d(tag, msg != null ? msg : NULL_STR);
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (LOG)
			Log.d(tag, msg != null ? msg : NULL_STR, tr);
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg != null ? msg : NULL_STR);
	}

	public static void e(String tag, String msg, Throwable tr) {
		try {
			Log.e(tag, msg != null ? msg : NULL_STR, tr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void v(String tag, String msg) {
		if (LOG)
			Log.v(tag, msg != null ? msg : NULL_STR);

	}

	public static void v(String tag, String msg, Throwable tr) {
		if (LOG)
			Log.v(tag, msg != null ? msg : NULL_STR, tr);
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg != null ? msg : NULL_STR);
	}

	public static void i(String tag, String msg, Throwable tr) {
		Log.i(tag, msg != null ? msg : NULL_STR, tr);
	}

	public static void w(String tag, String msg) {
		Log.w(tag, msg != null ? msg : NULL_STR);
	}

	public static void w(String tag, String msg, Throwable tr) {
		Log.w(tag, msg != null ? msg : NULL_STR, tr);
	}

	public static void p(String msg) {
		if (LOG)
			Log.i("Ymnl", msg != null ? msg : NULL_STR);
	}

	public static void e(String msg) {
		Log.e("Ymnl", msg != null ? msg : NULL_STR);
	}

}
