package com.wealoha.social.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import com.wealoha.social.BaseFragAct;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

	/**
	 * 判断当前应用程序处于前台还是后台
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(Context context) {
		android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description:应用是否在前台（应用的Activity在屏幕最前面算是前台）
	 * @param ctx
	 *            Context
	 * @return true表示前台
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public static boolean isAppForground(Context ctx) {
		ctx = ctx.getApplicationContext();

		RunningTaskInfo info = getCurrentTask(ctx);
		if (info == null) {
			return false;
		}

		return TextUtils.equals(ctx.getPackageName(), info.baseActivity.getPackageName());
	}

	/**
	 * @Description:获得当前运行的Task信息
	 * @param ctx
	 *            Context
	 * @return 当前运行的Task信息
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public static ActivityManager.RunningTaskInfo getCurrentTask(Context ctx) {
		ActivityManager localActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		if (localActivityManager == null) {
			return null;
		}
		List<RecentTaskInfo> listRecent = localActivityManager.getRecentTasks(1, 1);
		List<RunningTaskInfo> listRunning = localActivityManager.getRunningTasks(3); // SUPPRESS
																						// CHECKSTYLE
		if (listRecent == null || listRunning == null) {
			return null;
		}
		Iterator<RecentTaskInfo> recentIterator = listRecent.iterator();
		Iterator<RunningTaskInfo> runningIterator = listRunning.iterator();
		RunningTaskInfo obj = null;
		ActivityManager.RecentTaskInfo localRecentTaskInfo = null;
		ActivityManager.RunningTaskInfo firstRunningTaskInfo = null;
		// 先得到RecentTask
		if (recentIterator.hasNext()) {
			localRecentTaskInfo = recentIterator.next();
		}
		// 根据RecentTask的id和package进行判断
		if (localRecentTaskInfo == null) {
			return obj;
		}
		if (runningIterator.hasNext()) {
			firstRunningTaskInfo = runningIterator.next(); // running中的第一个Task
		}
		if (firstRunningTaskInfo == null) {
			return obj;
		}
		// 如果running中第一个的id==Recent.id，则firstRunningTask即为前台任务
		if (localRecentTaskInfo.id != -1 && firstRunningTaskInfo.id == localRecentTaskInfo.id) {
			// 最近任务存在且在runningTask列表中,则该任务即为当前任务
			obj = firstRunningTaskInfo;
		} else { // recent.id == -1或者recent.id != -1但是已经不在running列表中,
			// 根据packagename查找,找到第一个packagename不等于recentPackageName的runningtask
			String recentPackageName = null;
			if (localRecentTaskInfo.baseIntent != null && localRecentTaskInfo.baseIntent.getComponent() != null) {
				recentPackageName = localRecentTaskInfo.baseIntent.getComponent().getPackageName();
			}
			if (firstRunningTaskInfo.baseActivity != null && firstRunningTaskInfo.baseActivity.getPackageName() != null && !firstRunningTaskInfo.baseActivity.getPackageName().equals(recentPackageName)) {
				// 第一个running的packagename != recentPackageName
				obj = firstRunningTaskInfo;
			} else { // 从第二个和第三个running中找
				while (runningIterator.hasNext()) {
					ActivityManager.RunningTaskInfo localRunningTaskInfo = runningIterator.next();
					if (localRunningTaskInfo.baseActivity != null && localRunningTaskInfo.baseActivity.getPackageName() != null && !localRunningTaskInfo.baseActivity.getPackageName().equals(recentPackageName)) {
						obj = localRunningTaskInfo;
						break;
					}
				}
			}

		}
		return obj;
	}

	public static void openBrowser(BaseFragAct baseFragAct, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);

		intent.setData(content_url);
		baseFragAct.startActivity(intent);
	}

	public static int getVersionSdk() {
		return VERSION.SDK_INT;
	}

	public static String getLocaleLanguage() {
		Locale l = Locale.getDefault();
		return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}
}
