package com.wealoha.social.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.wealoha.social.R;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 播放音乐的服务
 * @copyright wealoha.com
 * @Date:2014-12-11
 */
public class MusicService extends Service {

	// 为日志工具设置标签
	private static String TAG = "MusicService";
	// 定义音乐播放器变量
	private MediaPlayer mPlayer;

	// 该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
	@Override
	public void onCreate() {
		mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dd);
		mPlayer.setLooping(false);
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		mPlayer.start();
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		mPlayer.stop();
		super.onDestroy();
	}

	// 其他对象通过bindService 方法通知该Service时该方法被调用
	@Override
	public IBinder onBind(Intent intent) {
		mPlayer.start();
		return null;
	}

	// 其它对象通过unbindService方法通知该Service时该方法被调用
	@Override
	public boolean onUnbind(Intent intent) {
		mPlayer.stop();
		return super.onUnbind(intent);
	}

}
