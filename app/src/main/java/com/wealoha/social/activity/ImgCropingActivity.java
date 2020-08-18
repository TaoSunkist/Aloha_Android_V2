package com.wealoha.social.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.topic.TopicDetailActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.ExifOrientation;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CropImage;
import com.wealoha.social.view.custom.CropImageView;

public class ImgCropingActivity extends BaseFragAct implements OnClickListener {

	private CropImageView mImageView;
	private Bitmap mBitmap;
	private float mThumbRatio; // 缩略图的对比原图的缩放比例
	private ExifOrientation mExifOrientation;

	private CropImage mCrop;

	private Button mSave;
	private Button mCancel, rotateLeft, rotateRight;
	private String mPath = "CropImageActivity";
	public static final String TAG = "CropImageActivity";
	public int screenWidth;
	public int screenHeight;

	private ProgressBar mProgressBar;
	private RelativeLayout mBackRl;
	public static final int SHOW_PROGRESS = 2000;
	private Context mContext;
	public static final int REMOVE_PROGRESS = 2001;
	private String mType;
	private String mOpenMethod;

	private static class ImgCropingHandler extends Handler {

		public WeakReference<ImgCropingActivity> act;

		public ImgCropingHandler(ImgCropingActivity icAct) {
			act = new WeakReference<ImgCropingActivity>(icAct);
		}

		@Override
		public void handleMessage(Message msg) {
			ImgCropingActivity icAct = act.get();
			if (icAct != null) {
				icAct.handlerService(msg);
			}
		}
	}

	public void handlerService(Message msg) {
		switch (msg.what) {
		case SHOW_PROGRESS:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case REMOVE_PROGRESS:
			mHandler.removeMessages(SHOW_PROGRESS);
			mProgressBar.setVisibility(View.INVISIBLE);
			break;
		}
	}

	private Handler mHandler = new ImgCropingHandler(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_cropimage);
		mContext = this;
		initView();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBitmap != null) {
			mBitmap = null;
		}
	}

	/**
	 * 获取屏幕的高和宽
	 */
	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void resetImageView(Bitmap b) {
		mImageView.clear();
		mImageView.setImageBitmap(b);
		mImageView.setImageBitmapResetBase(b, true);
		mImageView.invalidate();
		mCrop = new CropImage(this, mImageView, mHandler, b, mPath, mThumbRatio, mExifOrientation);
		// mCrop.crop(b);
	}

	@Inject
	ContextUtil mContextUtil;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cropimage_back_rl:
			// 如果是在Feed页面打开相机后进入裁剪界面,
			backLogic();
			break;
		case R.id.gl_modify_avatar_cancel:
			backLogic();
			break;
		case R.id.gl_modify_avatar_save:
			if (mCrop != null) {
				String path = null;
				Bitmap cropBitmap = mCrop.getCropImageFromOrig(GlobalConstants.ImageSize.MAX_FEED_IDEAL_SIZE); // 期望的尺寸
				// 1242
				if (cropBitmap != null) {
					path = mCrop.saveToLocal(cropBitmap, mContextUtil.getCurrentUser());
				}
				if (path != null) {
					Intent intent = new Intent();
					intent.putExtra("path", path);
					setResult(RESULT_OK, intent);
					overridePendingTransition(0, R.anim.right_out);
					finish();
				}
			}
			break;
		case R.id.gl_modify_avatar_rotate_left:
			mCrop.startRotate(270.f);
			break;
		case R.id.gl_modify_avatar_rotate_right:
			mCrop.startRotate(90.f);
			break;

		}
	}

	public void backLogic() {

		if (MainAct.TAG.equals(mType)) {
			MainAct mainAct = (MainAct) ActivityManager.isSaveStack(MainAct.class);
			if (mainAct != null) {
				if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(mOpenMethod)) {
					mainAct.openCamera(mainAct);
				} else {
					mainAct.openImgPick(mainAct);
				}
			}
		} else if (ConfigDetailsAct.TAG.equals(mType)) {
			ConfigDetailsAct configDetailsAct = (ConfigDetailsAct) ActivityManager.isSaveStack(ConfigDetailsAct.class);
			if (configDetailsAct != null) {
				if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(mOpenMethod)) {
					configDetailsAct.openCamera(configDetailsAct);
				} else {
					configDetailsAct.openImgPick(configDetailsAct);
				}
			}
		} else if (UserDataAct.TAG.equals(mType)) {
			UserDataAct userDataAct = (UserDataAct) ActivityManager.isSaveStack(UserDataAct.class);
			if (userDataAct != null) {
				if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(mOpenMethod)) {
					userDataAct.openCamera(userDataAct);
				} else {
					userDataAct.openImgPick(userDataAct);
				}
			}
		} else if (TopicDetailActivity.TAG.equals(mType)) {
			TopicDetailActivity topicDetailActivity = (TopicDetailActivity) ActivityManager.isSaveStack(TopicDetailActivity.class);
			if (topicDetailActivity != null) {
				if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(mOpenMethod)) {
					topicDetailActivity.openCamera(topicDetailActivity);
				} else {
					topicDetailActivity.openImgPick(topicDetailActivity);
				}
			}
		}
		finish();
		overridePendingTransition(0, R.anim.right_out);
	}

	public Bitmap getCompressionBitmap() {
		BitmapFactory.Options bfOptions = new BitmapFactory.Options();
		bfOptions.inDither = false;
		bfOptions.inPurgeable = true;
		bfOptions.inTempStorage = new byte[12 * 1024];
		// bfOptions.inJustDecodeBounds = true;
		File file = new File(mPath);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bmp = null;
		if (fs != null)
			try {
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fs != null) {
					try {
						fs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		return bmp;

	}

	public Bitmap createBitmap(String path) {
		int max = 3000;
		BitmapFactory.Options bOptions = new BitmapFactory.Options();
		bOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bOptions);
		int srcW = bOptions.outWidth;
		int srcH = bOptions.outHeight;
		double scale = Math.ceil(srcW / (double) srcH);
		// XL.d("createBitmap", msg)
		int sample = 1;
		int desW = 0, desH = 0;
		if (scale > 1) {
			desW = Math.min(srcW, max);
			sample = srcW / desW;
		} else {
			desH = Math.min(srcH, max);
			sample = srcH / desH;
		}
		bOptions.inJustDecodeBounds = false;
		bOptions.inPurgeable = true;
		bOptions.outHeight = desH;
		bOptions.outWidth = desW;
		bOptions.inInputShareable = true;
		bOptions.inSampleSize = sample;
		return BitmapFactory.decodeFile(path, bOptions);

	}

	private static interface SizeGetCallback {

		/**
		 * 实际缩放运算前被调用，用来获得缩放后的尺寸
		 * 
		 * @param srcW
		 *            原图
		 * @param srcH
		 *            原图
		 * @param thumbW
		 *            缩图
		 * @param thumbH
		 *            原图
		 * @param scaleRatio
		 *            缩放比例
		 */
		public void getSize(int srcW, int srcH, int thumbW, int thumbH, float scaleRatio);
	}

	/**
	 * 获取一张缩略图，使图像宽度尽量在给定范围内
	 * 
	 * @param path
	 * @param w
	 *            预期宽度
	 * @param h
	 *            预期高度
	 * @param cb
	 *            尺寸计算完毕后回调
	 * @return
	 */
	public Bitmap createThumbImage(String path, int w, int h, SizeGetCallback cb) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int srcWidth = opts.outWidth; // 获取图片的原始宽度
		int srcHeight = opts.outHeight; // 获取图片原始高度
		// XL.d(TAG, "获取缩略图，原图尺寸: " + srcWidth + "x" + srcHeight);

		float ratOrig = srcWidth / (float) srcHeight;
		float ratNew = w / (float) h;
		int wh = Math.min(w, h);
		int destW;
		int destH;
		if (srcWidth < wh || srcHeight < wh) {
			destW = srcWidth;
			destH = srcHeight;
		} else if (ratOrig < ratNew) {
			// 原图高多一些
			destH = h;
			destW = (int) Math.ceil(destH * srcWidth / (float) srcHeight);
		} else {
			destW = w;
			destH = (int) Math.ceil(destW * srcHeight / (float) srcWidth);
		}

		int r = srcWidth / destW;
		// 找到缩小最合适的倍数，缩放只能用2的倍数(1/2,1/4,1/8...)，找到个比预期稍大的
		// 直接使用按照sample采样获取的图，不再缩放了，减少缩放开销
		int sampleSize = 1;
		while (sampleSize <= r) {
			sampleSize *= 2;
		}

		sampleSize = Math.max(1, sampleSize / 2);

		// XL.d(TAG, "inSampleSize=" + sampleSize + " (" + r + ")");
		int sampleW = (int) Math.ceil(srcWidth / (float) sampleSize);
		int sampleH = (int) Math.ceil(srcHeight / (float) sampleSize);
		while (sampleW > 2600 || sampleH > 2600) {
			// 最后缩放出来的的图一定要小于3000
			// 否则可能oom
			sampleSize *= 2;
			sampleW = (int) Math.ceil(srcWidth / (float) sampleSize);
			sampleH = (int) Math.ceil(srcHeight / (float) sampleSize);
		}

		// XL.d(TAG, "缩放: " + srcWidth + "x" + srcHeight + "(orig) -> " //
		// + w + "x" + h + "(expect) -> " //
		// + destW + "x" + destH + "(best) -> " //
		// + "" + sampleW + "x" + sampleH + "(real)");

		cb.getSize(srcWidth, srcHeight, sampleW, sampleH, 1 / (float) sampleSize);
		opts = new BitmapFactory.Options();
		// 缩放的比例
		opts.inSampleSize = sampleSize;
		Bitmap decodeFile = BitmapFactory.decodeFile(path, opts);

		ExifOrientation orientation = ImageUtil.getOrientation(path);
		mExifOrientation = orientation;
		if (orientation != null && (orientation.flip || orientation.angle != 0)) {
			// 需要旋转
			XL.d(TAG, "旋转图片: flip=" + orientation.flip + ", angle=" + orientation.angle + ", path=" + path);
			Bitmap rotateFile = ImageUtil.rotateBitmap(decodeFile, orientation);
			if (rotateFile != decodeFile) {
				decodeFile.recycle();
			}
			decodeFile = rotateFile;

		}
		return decodeFile;
	}

	/**
	 * @Description:
	 * @param path
	 * @param w
	 * @param h
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-3
	 */
	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度c
			XL.d("createBitmap", srcWidth + "/" + srcHeight);
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;

			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / (double) w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / (double) h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			XL.d(TAG, destWidth + "/" + destHeight);
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// newOpts.inSampleSize = 2;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			newOpts.inInputShareable = true;
			newOpts.inPurgeable = true;
			newOpts.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			return null;
		}
	}

	public void initView() {
		mBackRl = (RelativeLayout) findViewById(R.id.cropimage_back_rl);
		mBackRl.setOnClickListener(this);
		getWindowWH();
		Bundle bundle = getIntent().getExtras();
		mPath = bundle.getString("path");// 图片的路径
		mType = bundle.getString("openType");// 打开的Activity是哪一个,通过Activity.TAG
		mOpenMethod = bundle.getString("openMethod");// 打开的裁剪的是相机还是图库
		// Log.i(TAG, "得到的图片的路径是 = " + mPath);
		mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
		mSave = (Button) this.findViewById(R.id.gl_modify_avatar_save);
		mCancel = (Button) this.findViewById(R.id.gl_modify_avatar_cancel);
		rotateLeft = (Button) this.findViewById(R.id.gl_modify_avatar_rotate_left);
		rotateRight = (Button) this.findViewById(R.id.gl_modify_avatar_rotate_right);
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		rotateLeft.setOnClickListener(this);
		rotateRight.setOnClickListener(this);
		try {
			// 1. 渲染裁切界面，使用屏幕宽度绘图
			// 2. 选择好范围后，计算裁切的实际范围，在原图上直接裁切

			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(mPath, options); // 此时返回的bitmap为null
			// mBitmap = createBitmap(mPath);
			// mBitmap = getCompressionBitmap();
			int thumbMaxWidth = (int) Math.ceil(UiUtils.getScreenWidth(mContext) * 2);
			int thumbMaxHeight = (int) Math.ceil(UiUtils.getScreenHeight(mContext) * 2);
			// XL.d(TAG, "渲染缩略图，用来选择裁切范围: " + thumbMaxWidth + "x" +
			// thumbMaxHeight);
			// 裁切框固定了
			// final int cropFrameSize = Math.min(thumbMaxWidth,
			// thumbMaxHeight);
			// mBitmap = createThumbImage(mPath, thumbMaxWidth, thumbMaxHeight,
			// new SizeGetCallback() {
			//
			// @Override
			// public void getSize(int srcW, int srcH, int thumbW, int thumbH,
			// float scaleRatio) {
			// // TODO，如果需要，可以先在这里显示裁切框，不用等图片加载完
			// mThumbRatio = scaleRatio; // 记录下缩放比例，切图后用
			// }
			// });

			mBitmap = new ImageUtil().createThumbImage(mPath, thumbMaxWidth, thumbMaxHeight, new ImageUtil.SizeGetCallback() {

				@Override
				public void getSize(int srcW, int srcH, int thumbW, int thumbH, float scaleRatio, ExifOrientation orientation) {
					mThumbRatio = scaleRatio;
					mExifOrientation = orientation;
				}
			});
			// XL.d(TAG, "缩放后的图片: ratio=" + mThumbRatio + ", bitmap=" +
			// mBitmap);
			// mBitmap = createBitmap(mPath, 2560, 2560);

			// mBitmap = getCompressionPic(options);
			// mBitmap = createBitmap(mPath, screenWidth, screenHeight);
			if (mBitmap != null) {
				resetImageView(mBitmap);
				mProgressBar = new ProgressBar(ImgCropingActivity.this);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, 400);
				params.gravity = Gravity.CENTER;
				addContentView(mProgressBar, params);
				mProgressBar.setVisibility(View.INVISIBLE);
				return;
			}
			Toast.makeText(mContext, R.string.failed, Toast.LENGTH_LONG).show();
			finish();
		} catch (Exception e) {
			Toast.makeText(mContext, R.string.Unkown_Error, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			backLogic();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
}
