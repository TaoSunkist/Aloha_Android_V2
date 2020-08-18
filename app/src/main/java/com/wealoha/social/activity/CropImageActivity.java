package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.view.custom.CropImage;
import com.wealoha.social.view.custom.CropImageView;

/**
 * 裁剪界面
 * 
 */
public class CropImageActivity extends BaseFragAct implements OnClickListener {

	private CropImageView mImageView;
	private Bitmap mBitmap;

	private CropImage mCrop;

	private Button mSave;
	private Button mCancel, rotateLeft, rotateRight;
	private String mPath = "CropImageActivity";
	private String TAG = "CropImageActivity";
	public int screenWidth = 300;
	public int screenHeight = 300;

	private ProgressBar mProgressBar;
	private RelativeLayout mBackRl;
	public static final int SHOW_PROGRESS = 2000;

	private String actUri;

	public static final int REMOVE_PROGRESS = 2001;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

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
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_cropimage);
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
		mCrop = new CropImage(this, mImageView, mHandler);
		mCrop.crop(b);
	}

	@Inject
	ContextUtil mContextUtil;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cropimage_back_rl:
			// openActivity(FunctionActivity.class);
			finish();
			overridePendingTransition(0, R.anim.right_out);
			break;
		case R.id.gl_modify_avatar_cancel:
			// mCrop.cropCancel();
			finish();
			overridePendingTransition(0, R.anim.right_out);
			break;
		case R.id.gl_modify_avatar_save:
			GlobalConstants.AppConstact.IS_UPDATE_HANDPIC = true;
			String path = mCrop.saveToLocal(mCrop.cropAndSave(), mContextUtil.getCurrentUser());
			Log.i(TAG, "截取后图片的路径是 = " + path);
			Bundle bundle = new Bundle();
			bundle.putString("path", path);
			// 判断谁打开了这个act
			if (GlobalConstants.IntentAction.INTENT_URI_USER_DATA.equals(actUri)) {
				setResult(RESULT_OK, new Intent().putExtras(bundle));
				finish();
				return;
			} else if (GlobalConstants.IntentAction.INTENT_URI_CONFIG_DETAILS.equals(actUri)) {
				setResult(RESULT_OK, new Intent().putExtras(bundle));
				startActivity(Uri.parse(actUri), bundle);
				finish();
				return;
			}
			// Toast.makeText(this, actUri, Toast.LENGTH_LONG).show();
			break;
		case R.id.gl_modify_avatar_rotate_left:
			mCrop.startRotate(270.f);
			break;
		case R.id.gl_modify_avatar_rotate_right:
			mCrop.startRotate(90.f);
			break;

		}
	}

	protected void addProgressbar() {
		mProgressBar = new ProgressBar(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 300);
		params.gravity = Gravity.CENTER;
		addContentView(mProgressBar, params);
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			return null;
		}
	}

	protected void findViewById() {

	}

	public void initView() {

		Bundle bundle = getIntent().getExtras();
		mPath = bundle.getString("path");
		actUri = bundle.getString("actUri");
		mBackRl = (RelativeLayout) findViewById(R.id.cropimage_back_rl);
		mBackRl.setOnClickListener(this);
		getWindowWH();

		Log.i(TAG, "得到的图片的路径是 = " + mPath);
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
			mBitmap = createBitmap(mPath, screenWidth, screenHeight);
			if (mBitmap == null) {
				Toast.makeText(CropImageActivity.this, R.string.cant_find_image, 0).show();
				finish();
			} else {
				resetImageView(mBitmap);
			}
		} catch (Exception e) {
			Toast.makeText(CropImageActivity.this, R.string.cant_find_image, 0).show();
			finish();
		}
		addProgressbar();
	}

}
