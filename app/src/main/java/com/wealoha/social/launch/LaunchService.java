package com.wealoha.social.launch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.app.IntentService;
import android.content.Intent;

import com.wealoha.social.utils.XL;

/**
 * <h1>App启动服务，监听指定端口通过HTTP协议启动</h1>
 * 
 * 支持的url
 * <ul>
 * <li>/user?userId=xx&shareby=xx, shareby可选</li>
 * </ul>
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2015-1-5 下午10:24:45
 */
public class LaunchService extends IntentService {

	private final String TAG = getClass().getSimpleName();
	// Defines a custom Intent action
	public static final String BROADCAST_ACTION = "com.wealoha.social.launch.BROADCAST";

	// Defines the key for the status "extra" in an Intent
	public static final String EXTENDED_DATA_URL = "com.wealoha.social.launch.URL";

	private LaunchHttpServer server;

	public LaunchService() {
		super("LaunchService");
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		XL.i(TAG, "启动http服务: 5050");

		server = new LaunchHttpServer(this, 5050);
		try {
			server.start();
		} catch (IOException e) {
			XL.w(TAG, "服务启动异常", e);
		}
		// 不能退出，否则服务会停止
		while (true) {
			try {
				TimeUnit.HOURS.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void onDestroy() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				XL.w(TAG, "服务停止异常", e);
			}
		}
		super.onDestroy();
	}

}
