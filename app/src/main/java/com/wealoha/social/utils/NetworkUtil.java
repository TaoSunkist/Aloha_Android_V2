package com.wealoha.social.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.wealoha.social.AppApplication;


/**
 * 判断网络情况工具类
 * @Description:
 * @author:zhangqian  
 * @see:   
 * @since:      
 * @copyright © jrzj.com
 * @Date:2014-6-17
 */
public class NetworkUtil {

	/**
	 * wifi 是否连接或正在连接
	 * @return
	 * @see: 
	 * @since: 
	 */
	public static boolean isWifi() {
		try {
			ConnectivityManager conMan = (ConnectivityManager) AppApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifi == State.CONNECTING || wifi == State.CONNECTED) {
				return true;
			}
		} catch (Exception e) {
			XL.e("failfast", "failfast_AA", e);
			return false;
		}
		return false;
	}

	/**
	 * 判断连接的网络是否是2g/3g
	 * @return
	 */
	public static boolean isMobile() {
		try {
			ConnectivityManager conMan = (ConnectivityManager) AppApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifi != State.CONNECTED) {
				State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
				if (mobile == State.CONNECTED) {
					return true;
				}
			}
		} catch (Exception e) {//java.lang.NullPointerException
			XL.e("failfast", "failfast_AA", e);
			return false;
		}
		return false;

	}

	/**
	 * 判断是否有网络连接
	 * @return
	 * @see:
	 * @since:
	 */
	public static boolean isNetworkAvailable() {
		try {
			ConnectivityManager connect = (ConnectivityManager) AppApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connect == null) {
				return false;
			} else {
				NetworkInfo[] info = connect.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			XL.e("failfast", "failfast_AA", e);
			return false;
		}
		return false;
	}
}
