package com.wealoha.social.inject;

import javax.inject.Singleton;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import com.wealoha.social.AppApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Android系统的模块
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-28 下午3:25:07
 */
@Module(complete = true, library = true)
public class AndroidModule {

	@Provides
	@Singleton
	Context provideAppContext() {
		return AppApplication.getInstance().getApplicationContext();
	}

	@Provides
	SharedPreferences provideDefaultSharedPreferences(final Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Provides
	PackageInfo providePackageInfo(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	TelephonyManager provideTelephonyManager(Context context) {
		return getSystemService(context, Context.TELEPHONY_SERVICE);
	}

	@SuppressWarnings("unchecked")
	public <T> T getSystemService(Context context, String serviceConstant) {
		return (T) context.getSystemService(serviceConstant);
	}

	@Provides
	InputMethodManager provideInputMethodManager(final Context context) {
		return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Provides
	ApplicationInfo provideApplicationInfo(final Context context) {
		return context.getApplicationInfo();
	}

	@Provides
	AccountManager provideAccountManager(final Context context) {
		return AccountManager.get(context);
	}

	@Provides
	ClassLoader provideClassLoader(final Context context) {
		return context.getClassLoader();
	}

	@Provides
	NotificationManager provideNotificationManager(final Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Provides
	LayoutInflater provideLayoutInflater(final Context context) {
		return LayoutInflater.from(context);
	}

	@Provides
	ActivityManager provideActivityManager(final Context context) {
		return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}
}
