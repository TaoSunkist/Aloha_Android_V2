package com.wealoha.social;

import javax.inject.Inject;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.wealoha.social.fragment.LoadingFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

/**
 * 监听网络状态变更
 * 
 * @author superman <superman@cuapp.me>
 * @see
 * @since
 * @date 2014-12-12 11:23:09
 */
public class ConnectionChangeRecevier extends BroadcastReceiver {

	State wifiState = null;
	State mobileState = null;
	@Inject
	ContextUtil contextUtil;

	private String TAG = getClass().getSimpleName();

	public ConnectionChangeRecevier() {
		Injector.inject(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		XL.i("CONNECTION_TEST", "CONNECTION");
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			XL.i("CONNECTION_TEST", "connectivityManager not null");
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetInfo != null) {
				// activeNetInfo.getTypeName();
				XL.i("CONNECTION_TEST", "activeNetInfo not null");
				// NetworkInfo mobNetInfo = null;
				// try {
				// mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				// } catch (NullPointerException e) {
				// mobNetInfo = getConnectManager(context).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				// }
				// if (activeNetInfo != null && mobNetInfo != null) {
				// if (!activeNetInfo.isAvailable() && !mobNetInfo.isAvailable()) {
				// ToastUtil.shortToast(context, "網絡連接不可用，請稍後重試");
				// }
				// }
				if (activeNetInfo != null) {
					Fragment frag = contextUtil.getForegroundFrag();
					if (frag instanceof LoadingFragment) {
						((LoadingFragment) frag).onResume();
					}
				}

				// NetworkInfo networkWifiInfo = null;
				// try {
				// networkWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				// } catch (NullPointerException e) {
				// networkWifiInfo = getConnectManager(context).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				// }
				// if (networkWifiInfo != null) {
				// wifiState = networkWifiInfo.getState();
				// }
				// NetworkInfo networkMobileInfo = null;
				// try {
				// networkMobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				// } catch (NullPointerException e) {
				// networkMobileInfo =
				// getConnectManager(context).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				// }
				// if (networkMobileInfo != null) {
				// mobileState = networkMobileInfo.getState();
				// }
				// if (wifiState != null && mobileState != null && State.CONNECTED != wifiState &&
				// State.CONNECTED != mobileState) {
				// // context.startService(intent2);
				// ToastUtil.shortToast(context, "網絡連接不可用，請稍後重試");
				// }

				// 测试API
				XL.i(TAG, "网络状态变更，重新测试api");
				AppApplication.getInstance().selectApiEndpoint();
			} else {
				ToastUtil.shortToast(context,R.string.network_error);
			}
		}
	}

	ConnectivityManager getConnectManager(Context context) {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

}
