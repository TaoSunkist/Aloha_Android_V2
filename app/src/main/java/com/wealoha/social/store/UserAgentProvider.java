package com.wealoha.social.store;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import timber.log.Timber;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.XL;

/**
 * 
 * 
 * @author walker
 * @see
 * @since
 * @date 2014-11-12 下午4:30:03
 */
public class UserAgentProvider {

	private static final String TAG = UserAgentProvider.class.getSimpleName();

	@Inject
	protected PackageInfo info;
	@Inject
	protected TelephonyManager telephonyManager;
	@Inject
	Context context;

	protected String userAgent;
	protected String device;
	protected String acceptLanguage;

	public UserAgentProvider() {
		Injector.inject(this);
	}

	// Aloha/1.0.2 (Android (19/4.4.2; 480dpi; 1080x1776; LGE/google; Nexus 5; hammerhead; hammerhead)
	private static final String APP_NAME = "Aloha";

	public String getDevice() {
		if (device == null) {
			synchronized (UserAgentProvider.class) {
				device = String.format("Android %d/%s; %s; %s; %s; %s; %s; %s/%d/%s;", //
										Build.VERSION.SDK_INT, //
										Build.VERSION.RELEASE, //
										StringUtil.capitalize(Build.MANUFACTURER), //
										StringUtil.capitalize(Build.DEVICE), //
										StringUtil.capitalize(Build.BRAND), //
										StringUtil.capitalize(Build.MODEL), //
										StringUtil.capitalize(getSimOperatorName()), //
										info.versionName, //
										info.versionCode, //
										getAndroidStore());
			}
		}
		return device;
	}

	private String getSimOperatorName() {
		if (telephonyManager != null) {
			if (StringUtils.isNotEmpty(telephonyManager.getSimOperatorName())) {
				try {
					return URLEncoder.encode(telephonyManager.getSimOperatorName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					XL.w(TAG, "编码错误", e);
				}
			}
		}
		return "not-found";
	}

	// (\d+)\.(\d+)\.(\d+)
	private Pattern versionPattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:_dev){0,1}$");

	public String getAcceptLanguage() {
		if (acceptLanguage == null) {
			synchronized (UserAgentProvider.class) {
				if (acceptLanguage == null) {
					acceptLanguage = context.getResources().getConfiguration().locale.toString();
				}
			}
		}
		return acceptLanguage;
	}

	public String getUserAgent() {
		if (userAgent == null) {
			synchronized (UserAgentProvider.class) {
				if (userAgent == null) {
					userAgent = String.format("%s/%s (%s)", //
												APP_NAME, //
												info.versionName, //
												getDevice());
				}
			}
		}

		return userAgent;
	}

	private String store;
	private Pattern p = Pattern.compile("aloha_store_(\\w+)");

	/**
	 * 获取当前app配置的应用商店
	 * 
	 * @return
	 */
	public String getAndroidStore() {
		if (store != null) {
			return store;
		}
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				Matcher m = p.matcher(entryName);
				if (m.find()) {
					store = m.group(1);
					Timber.i("应用来自商店: %s", store);
					break;
				}
			}
		} catch (IOException e) {
			Timber.e(e, "解析商店失败");
		} finally {
			// java.lang.IncompatibleClassChangeError: interface not implemented
			// at org.apache.commons.io.IOUtils.closeQuietly(IOUtils.java:303)
			// at com.gethehe.android.uitls.UserAgentProvider.getAndroidStore(UserAgentProvider.java:147)

			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (Exception e) {
					Timber.w(e, "关闭失败");
				}
			}

		}
		if (store == null) {
			store = "";
		}
		return store;
	}

	// public String getAndroidStore() {
	// try {
	// ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
	// PackageManager.GET_META_DATA);
	// if (appInfo.metaData != null) {
	// String store = appInfo.metaData.getString("ANDROID_STORE");
	// if (StringUtil.isNotEmpty(store)) {
	// XL.d(TAG, "应用商店: " + store);
	// }
	// return store;
	// }
	// } catch (Exception e) {
	// XL.w(TAG, "取不到应用商店设置", e);
	// }
	// return null;
	// }

	/**
	 * 获取应用内部版本号
	 * 
	 * @return
	 */
	public String getInternalVersion() {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo.metaData != null) {
				String version = appInfo.metaData.getString("INTERNAL_VERSION");
				if (StringUtil.isNotEmpty(version)) {
					Matcher m = versionPattern.matcher(version);
					if (m.find()) {
						// XL.d(TAG, "内部版本号: " + version);
						return version;
					} else {
						throw new RuntimeException("内部版本号配置错误，请检查 AndroidManifest配置的INTERNAL_VERSION: " + version);
					}
				}
			}
		} catch (Exception e) {
			// XL.w(TAG, "取不到内部版本号设置", e);
		}
		return "1.2.0";
	}
	// public String getInternalVersion() {
	// try {
	// ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
	// PackageManager.GET_META_DATA);
	// if (appInfo.metaData != null) {
	// String version = appInfo.metaData.getString("INTERNAL_VERSION");
	// if (StringUtil.isNotEmpty(version)) {
	// Matcher m = versionPattern.matcher(version);
	// if (m.find()) {
	// XL.d(TAG, "内部版本号: " + version);
	// return version;
	// } else {
	// throw new RuntimeException("内部版本号配置错误，请检查 AndroidManifest配置的INTERNAL_VERSION: " + version);
	// }
	// }
	// }
	// } catch (Exception e) {
	// XL.w(TAG, "取不到内部版本号设置", e);
	// }
	// return "1.2.0";
	// }

}
