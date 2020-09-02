package com.wealoha.social.adapter.feed;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.Post;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.OnClickListenerUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;

public class VideoFeedHolder extends BaseFeedHolder implements OnCompletionListener,//
		OnPreparedListener, OnVideoSizeChangedListener,//
		OnBufferingUpdateListener, OnErrorListener, OnInfoListener {


	private AudioManager audioManager;

	public ViewGroup rootView;
	public ImageView imageView;
	public Button button;
	public RelativeLayout item_feed_first_photo_rl;
	public final int videoWidth;
	public Post mPost;
	public LayoutInflater mInflater;
	public ViewGroup mParent;
	public RelativeLayout.LayoutParams params;
	private MediaPlayer mMediaPlayer;
	private Fragment mFrag;

	private TextureView textureView;
	private ImageView videoImgView;
	private RelativeLayout coverRoot;
	private ProgressBar progressBar;
	private TextView volmTextView;
	private ImageView praiseImg;
	private FrameLayout mediaplayerRoot;
	private ImageView startImg;

	private AlphaAnimation alphaAnim;
	private static int y1;// 视频播放的临界点
	private static int y2;// 视频停止临界点

	public VideoFeedHolder(LayoutInflater inflater, ViewGroup parent, Fragment frag) {
		super();
		Injector.inject(this);
		mInflater = inflater;
		mParent = parent;
		mFrag = frag;
		videoWidth = UiUtils.getScreenWidth(mFrag.getActivity().getApplicationContext());
		rootView = (ViewGroup) mInflater.inflate(R.layout.holder_feed_video, mParent, false);
		textureView = (TextureView) rootView.findViewById(R.id.textureview);
		videoImgView = (ImageView) rootView.findViewById(R.id.video_image);
		coverRoot = (RelativeLayout) rootView.findViewById(R.id.cover_root);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
		volmTextView = (TextView) rootView.findViewById(R.id.sound_switch);
		praiseImg = (ImageView) rootView.findViewById(R.id.item_feed_parise_iv);
		mediaplayerRoot = (FrameLayout) rootView.findViewById(R.id.mediaplayer_root);
		startImg = (ImageView) rootView.findViewById(R.id.start_image);
		initDoubleClickListener(mediaplayerRoot, 300);

		textureView.getLayoutParams().height = videoWidth;
		videoImgView.getLayoutParams().height = videoWidth;
		mediaplayerRoot.getLayoutParams().height = videoWidth;
		y1 = UiUtils.dip2px(mFrag.getActivity(), 300);
		y2 = UiUtils.dip2px(mFrag.getActivity(), 400);

		alphaAnim = new AlphaAnimation(1, 0);
		alphaAnim.setDuration(500);
		alphaAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				XL.i("alpha_anim_test", "s:" + volmTextView.getVisibility() + ":" + volmTextView.getAlpha());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				volmTextView.setVisibility(View.INVISIBLE);
				XL.i("alpha_anim_test", "e:" + volmTextView.getVisibility() + ":" + volmTextView.getAlpha());
			}
		});

		audioManager = (AudioManager) mFrag.getActivity().getSystemService(Context.AUDIO_SERVICE);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setVolume(0f, 0f);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		MySurfaceTextureListener mySurfaceTextureListener = new MySurfaceTextureListener();
		textureView.setClickable(true);
		textureView.setSurfaceTextureListener(mySurfaceTextureListener);
		textureView.setOnClickListener(mySurfaceTextureListener);

	}

	@Override
	public View resetViewData(Post post) {
		mPost = post;
		setSourceData();
		return rootView;
	}

	@Override
	public View resetViewData(Post post, int holderPosition) {
		mPost = post;
		setSourceData();
		return rootView;
	}

	private void setSourceData() {
		coverRoot.setVisibility(View.VISIBLE);
		Picasso.get().load(mPost.getCommonImage().getUrlSquare(ImageSize.FEED_MAX)).into(videoImgView);
	}

	@Override
	public View getView() {
		return rootView;
	}

	/***
	 * 初始化单击和双击事件
	 * 
	 * @param view
	 * @param clickInterval
	 *            区分单击和双击事件的时间间隔
	 * @return void
	 */
	private void initDoubleClickListener(View view, long clickInterval) {
		if (view != null) {
			view.setOnClickListener(new OnClickListenerUtil(clickInterval) {

				@Override
				public void OnDoubleClickEvent(View v) {
					if (parentHolder != null) {
						parentHolder.praisePost();
					}
				}

				@Override
				public void OnClickEvent(View v) {
					voiceControl();
				}
			});
		}
	}

	/***
	 * 声音控制
	 * 
	 * @return void
	 */
	private void voiceControl() {
		if (mMediaPlayer != null) {
			float current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			current /= 15;
			mMediaPlayer.setVolume(current, current);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		XL.i("VIDEO_TEST", "onPrepared");
		coverRoot.setVisibility(View.GONE);
		volmTextView.postDelayed(new Runnable() {

			@Override
			public void run() {
				volmTextView.startAnimation(alphaAnim);
			}
		}, 3000);

		mp.start();
		XL.i("VIDEO_TEST", "start");
	}

	public void stopPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
		volmTextView.clearAnimation();
		coverRoot.setVisibility(View.VISIBLE);
		startImg.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		XL.i("SunkistY", "Stop開始停止操作");
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isPlayer() {
		XL.d("SunkistY", getRawTopY() + "," + getRawBottomY());
		if (getRawTopY() < y1 && getRawBottomY() > y2) {
			return true;
		} else {
			return false;
		}
	}

	public int getRawTopY() {
		int[] location = new int[2];
		rootView.getLocationOnScreen(location);
		return location[1];
	}

	@Override
	public int getRawBottomY() {
		return getRawTopY() + videoWidth;
	}

	/**
	 * @Description:开始播放
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月8日
	 */
	public void startMediaPlayer() {
		if (mMediaPlayer.isPlaying()) {
			return;
		}
		volmTextView.setVisibility(View.VISIBLE);
		coverRoot.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		startImg.setVisibility(View.GONE);
		mMediaPlayer.reset();
		mMediaPlayer.setVolume(0f, 0f);
		try {
			mMediaPlayer.setDataSource(mPost.getCommonVideo().getUrl());
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			progressBar.setVisibility(View.GONE);
			// ToastUtil.longToast(mFrag.getActivity(), R.string.play_failed);
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			mMediaPlayer.reset();// 可调用此方法重置
		} else if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
		} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
		} else if (what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
			startMediaPlayer();
		}
		return false;// 返回false，表示错误没有被处理
	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description TextureView的活動监听器和點擊監聽器
	 * @copyright wealoha.com
	 * @Date:2015年4月3日
	 */
	class MySurfaceTextureListener implements SurfaceTextureListener, View.OnClickListener {

		public MySurfaceTextureListener() {

		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			stopPlayer();
			return true;
		}

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			if (mMediaPlayer != null) {
				mMediaPlayer.setSurface(new Surface(surface));
			}
		}

		@Override
		public void onClick(View v) {

		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		XL.i("VIDEO_TEST", "onBufferingUpdate:" + percent);
	}

	@Override
	public void praisePost() {
		praiseImg.startAnimation(AnimationUtils.loadAnimation(mFrag.getActivity(), R.anim.feed_like));
	}

	@Override
	public void removeTag(String userid) {

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	protected void finalize() throws Throwable {
		if (mMediaPlayer != null) {
			// 释放播放器
			mMediaPlayer.release();
		}
		super.finalize();
	}
}
