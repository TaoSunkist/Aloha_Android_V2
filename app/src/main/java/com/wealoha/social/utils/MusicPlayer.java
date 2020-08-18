package com.wealoha.social.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.wealoha.social.AppApplication;
import com.wealoha.social.R;

public class MusicPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

	private Context context;
	private MediaPlayer bgPlayer; // 播放背景音乐的播放器
	private SoundPool actionMusicPlayer; // 播放音效的播放器
	private int source_da, source_givecard, source_start, source_win, source_calllord; // 各种音效的source

	public MusicPlayer(Context context) { // 初始化
		if (context == null) {
			this.context = AppApplication.getInstance();
		}
		this.context = context;
		this.actionMusicPlayer = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5); // 这里指定声音池的最大音频流数目为10，声音品质为5大家可以自己测试感受下效果
		this.source_da = actionMusicPlayer.load(this.context, R.raw.dd, 0);
		this.source_givecard = actionMusicPlayer.load(this.context, R.raw.dd, 0);
		this.source_start = actionMusicPlayer.load(this.context, R.raw.dd, 0);
		this.source_win = actionMusicPlayer.load(this.context, R.raw.dd, 0);
		this.source_calllord = actionMusicPlayer.load(this.context, R.raw.dd, 0);
	}

	public void onCompletion(MediaPlayer mp) { // 音频文件播放完成时自动调用

	}

	public boolean onError(MediaPlayer mp, int what, int extra) { // 音频文件播放发生错误时自动调用
		return false;
	}

	public void playBgSound(int paramInt) { // 播放背景音乐，paramInt为音频文件ID

		try {
			MediaPlayer mediaPlayer = MediaPlayer.create(context, paramInt);
			bgPlayer = mediaPlayer;
			bgPlayer.setOnCompletionListener(this);
			bgPlayer.setLooping(false); // 设置是否循环，一般背景音乐都设为true
			bgPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public MediaPlayer getBackGroundPlayer() {
		return this.bgPlayer;
	}

	public void stopBgSound() { // 停止背景音乐的播放
		if (bgPlayer == null)
			return;
		if (bgPlayer.isPlaying())
			bgPlayer.stop();
		bgPlayer.release();
		bgPlayer = null;
	}

	private void playSound(int source) { // 如果系统设置中开启了音效，则播放相关的音频文件
		// if (SysSetting.getInstance(context).isEffectSound()) {
		actionMusicPlayer.play(source, 1, 1, 0, 0, 1);
		// }
	}

	public void playHitCardSound() {
		playSound(source_da);
	}

	public void playGiveCardSound() {
		playSound(source_givecard);
	}

	public void playStartSound() {
		playSound(source_start);
	}

	public void playWinSound() {
		playSound(source_win);
	}

	public void playCalllordSound() {
		playSound(source_calllord);
	}
}
