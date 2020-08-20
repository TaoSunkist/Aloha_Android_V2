package com.wealoha.social.activity;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import butterknife.InjectView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.view.custom.dialog.FlippingLoadingDialog;
import com.wealoha.social.zxing.BitmapDecoder;
import com.wealoha.social.zxing.BitmapUtils;
import com.wealoha.social.zxing.CameraManager;
import com.wealoha.social.zxing.CaptureActivityHandler;
import com.wealoha.social.zxing.FinishListener;
import com.wealoha.social.zxing.InactivityTimer;
import com.wealoha.social.zxing.IntentSource;
import com.wealoha.social.zxing.ViewfinderView;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a viewfinder
 * to help the user place the barcode correctly, shows feedback as the image processing is happening, and then
 * overlays the results when a scan is successful.
 * 
 * 此Activity所做的事： 1.开启camera，在后台独立线程中完成扫描任务； 2.绘制了一个扫描区（viewfinder）来帮助用户将条码置于其中以准确扫描； 3.扫描成功后会将扫描结果展示在界面上。
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends BaseFragAct implements SurfaceHolder.Callback,
		View.OnClickListener {

	private static final int PARSE_BARCODE_FAIL = 300;
	private static final int PARSE_BARCODE_SUC = 200;
	@Inject
	ServerApi mProfileService;
	@Inject
	ContextUtil mContextUtil;
	@InjectView(R.id.ewm_iv)
	ImageView mEwmIv;
	private User mUser;
	final int DISPLAY_TWO_DIMENSIONAL_CODE = 0x00000001;
	/**
	 * 是否有预览
	 */
	private boolean mHasSurface;

	/**
	 * 活动监控器。如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;

	/**
	 * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
	 */
	// private BeepManager beepManager;
	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	// private AmbientLightManager ambientLightManager;

	private CameraManager cameraManager;
	/**
	 * 扫描区域
	 */
	private ViewfinderView viewfinderView;

	private CaptureActivityHandler handler;

	private Result lastResult;

	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 编码类型，该参数告诉扫描器采用何种编码方式解码，即EAN-13，QR Code等等
	 * 对应于DecodeHintType.POSSIBLE_FORMATS类型 参考DecodeThread构造函数中如下代码：hints.put(DecodeHintType.POSSIBLE_FORMATS,
	 * decodeFormats);
	 */
	private Collection<BarcodeFormat> decodeFormats;

	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 该参数最终会传入MultiFormatReader，
	 * 上面的decodeFormats和characterSet最终会先加入到decodeHints中 最终被设置到MultiFormatReader中
	 * 参考DecodeHandler构造器中如下代码：multiFormatReader.setHints(hints);
	 */
	private Map<DecodeHintType, ?> decodeHints;
	/**
	 * 【辅助解码的参数(用作MultiFormatReader的参数)】 字符集，告诉扫描器该以何种字符集进行解码 对应于DecodeHintType.CHARACTER_SET类型
	 * 参考DecodeThread构造器如下代码：hints.put(DecodeHintType.CHARACTER_SET, characterSet);
	 */
	private String characterSet;
	private Result savedResultToShow;
	private IntentSource source;
	private Handler mHandler = new MyHandler(this);

	class MyHandler extends Handler {

		private WeakReference<Activity> activityReference;

		public MyHandler(Activity activity) {
			activityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case PARSE_BARCODE_SUC: // 解析图片成功
				// msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case PARSE_BARCODE_FAIL:// 解析图片失败
				Toast.makeText(activityReference.get(), getResources().getString(R.string.qr_code_not_found), Toast.LENGTH_SHORT).show();

				break;
			}
			super.handleMessage(msg);
		}

	}

	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.capture);
		mUser = mContextUtil.getCurrentUser();
		// path = FileTools.getAlohaImgPath() + mUser.id + ".jpg";
		mHasSurface = false;
		mContext = this;
		inactivityTimer = new InactivityTimer(this);
		// beepManager = new BeepManager(this);
		// ambientLightManager = new AmbientLightManager(this);
		mLoadingDialog = new FlippingLoadingDialog(mContext, R.layout.popup_prompt, new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// 监听图片识别按钮
		findViewById(R.id.capture_scan_photo).setOnClickListener(this);
		findViewById(R.id.capture_flashlight).setOnClickListener(this);
		// 获取在当前窗口内的绝对坐标
		// 本地是否存在生成的二维码尺寸

		mEwmSize = ContextConfig.getInstance().getIntWithFilename(GlobalConstants.AppConstact.EWM_SIZE);
		if (mEwmSize > 0)
			init(mEwmSize);
	}

	private void init(final int ewmSize) {

		AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new java.lang.Runnable() {

			private Bitmap bit;
			private Bitmap bitmap;

			@Override
			public void run() {
				String url = StringUtil.shareWebPagerUrl(mUser, null, StringUtil.CREATE_ERWEIMA);
				bitmap = ImageUtil.createTwoDimentsionalCode(CaptureActivity.this, url, null, ewmSize);

				mHandler.post(new java.lang.Runnable() {

					@Override
					public void run() {
						mEwmIv.setLayoutParams(new RelativeLayout.LayoutParams(mEwmSize + 100, mEwmSize + 100));
						// mEwmIv.setImageBitmap(bitmap);
						// mEwmIv.setBackgroundDrawable(new
						// BitmapDrawable(getResources(), bitmap));
						setViewBack(mEwmIv, bitmap);
					}
				});
				if (mUser != null && NetworkUtil.isNetworkAvailable()) {
					Bitmap thumb = ImageUtil.handPic(ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop));
					if (thumb != null) {
						thumb = ImageUtil.drawRound(thumb, (int) (CaptureActivity.this.mScreenWidth * 0.009f + 0.5f));
						bit = ImageUtil.getTwoDimentsionalCode(bitmap, thumb);
					} else {
						return;
					}
					mHandler.post(new java.lang.Runnable() {

						@Override
						public void run() {
							if (bit != null) {
								mEwmIv.setLayoutParams(new RelativeLayout.LayoutParams(mEwmSize + 100, mEwmSize + 100));
								setViewBack(mEwmIv, bit);
							}
						}
					});
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	// 防止sdk8的设备初始化预览异常
	@Override
	protected void onResume() {
		super.onResume();
		if (null != cameraManager && cameraManager.isOpen())
			return;
		// 相机初始化的动作需要开启相机并测量屏幕大小，这些操作
		// 不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的 话，
		// 会导致扫描窗口的尺寸计算有误的bug
		if (mEwmSize <= 0) {// 如果生成二维码的尺寸≤0,那么需要回调并且生成二维码的图片
			cameraManager = new CameraManager(this, new setOnChageEwmSize() {

				public void onChageEwmSize(int topOffSet, int bottomOffSet) {
					mEwmSize = bottomOffSet - topOffSet;
					// 不需要每次都进行等待扫描框的高度计算，保存在本地;
					ContextConfig.getInstance().putIntWithFilename(GlobalConstants.AppConstact.EWM_SIZE, mEwmSize);
					init(mEwmSize);
				}
			});
		} else {
			cameraManager = new CameraManager(this);
		}

		viewfinderView = (ViewfinderView) findViewById(R.id.capture_viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		handler = null;
		lastResult = null;
		// 摄像头预览功能必须借助SurfaceView，因此也需要在一开始对其进行初始化
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view); // 预览
		final SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (mHasSurface) {
			initCamera(surfaceHolder);
		} else {
			if (Utils.getVersionSdk() < 10) {// 防止sdk8的设备初始化预览异常
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			surfaceHolder.addCallback(this);
		}

		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;

	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		// ambientLightManager.stop();
		// beepManager.close();
		// 关闭摄像头
		cameraManager.closeDriver();
		if (!mHasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE) && lastResult != null) { // 重新进行扫描
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.zoomIn();
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.zoomOut();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	String gotPhotoPath = null;

	private int mEwmSize;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (resultCode == RESULT_OK) {
			final ProgressDialog progressDialog;
			switch (requestCode) {
			case GlobalConstants.AppConstact.PHOTO_PICKED_WITH_DATA:

				if (intent == null) {// NullPointerException
					return;
				}
				Cursor cursor = null;
				try {
					Uri uri = intent.getData();
					String alamPath = (uri != null ? uri.toString() : "");
					if (alamPath != null && alamPath.startsWith("content://media/")) {
						String[] proj = { MediaStore.Images.Media.DATA };
						cursor = getContentResolver().query(uri, proj, null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						gotPhotoPath = (cursor != null ? cursor.getString(column_index) : "");
					} else {
						gotPhotoPath = (uri != null ? uri.getPath() : "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}

				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage(getResources().getString(R.string.loading));
				progressDialog.setCancelable(false);
				progressDialog.show();
				AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new java.lang.Runnable() {

					@Override
					public void run() {
						Bitmap img = BitmapUtils.getCompressedBitmap(gotPhotoPath);

						BitmapDecoder decoder = new BitmapDecoder(CaptureActivity.this);
						Result result = decoder.getRawResult(img);

						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = ResultParser.parseResult(result).toString();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							mHandler.sendMessage(m);
						}

						progressDialog.dismiss();

					}
				});

				break;

			}
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (popupJoinEwm != null && popupJoinEwm.isShowing()) {
			popupJoinEwm.dismiss();
		}
		// Log.e("surfaceCreated",
		// "*** WARNING *** surfaceCreated() gave us a null surface!");'
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
	}

	/**
	 * A valid barcode has been found, so give an indication of success and show the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		// 重新计时
		inactivityTimer.onActivity();
		lastResult = rawResult;
		// 把图片画到扫描框
		// viewfinderView.drawResultBitmap(barcode);
		if (rawResult != null) {
			// beepManager.playBeepSoundAndVibrate();
			String string = ResultParser.parseResult(rawResult).toString();
			if (!(TextUtils.isEmpty(string)) && string.contains("http://www.wealoha.com/user")) {
				String userId = string.substring(string.lastIndexOf('/') + 1);
				if (!TextUtils.isEmpty(userId)) {
					if (mLoadingDialog != null) {
						mLoadingDialog.show();
					}
					mProfileService.getUserProfile(userId, new Callback<com.wealoha.social.beans.Result<ProfileData>>() {

						@Override
						public void success(com.wealoha.social.beans.Result<ProfileData> result, Response arg1) {
							mLoadingDialog.dismiss();
							if (result == null) {
								return;
							}
							if (result.isOk()) {
								Bundle bundle = new Bundle();
								result.data.user.setMe(false);
								bundle.putParcelable(User.TAG, result.data.user);
								bundle.putString("refer_key", GlobalConstants.WhereIsComeFrom.SCANNER_TO_PROFILE);
								startFragment(Profile2Fragment.class, bundle, true);
								finish();
							} else {
								ToastUtil.shortToast(mContext, getResources().getString(R.string.qr_code_not_found));

								restartPreviewAfterDelay(1000l);
							}
						}

						@Override
						public void failure(RetrofitError arg0) {
							mLoadingDialog.dismiss();
							ToastUtil.shortToast(mContext, getResources().getString(R.string.qr_code_not_found));
							restartPreviewAfterDelay(1000l);
						}
					});

					return;
				}
			}
		}
		restartPreviewAfterDelay(1000l);
		ToastUtil.shortToast(mContext, getResources().getString(R.string.qr_code_not_found));
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}

		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (Exception ioe) {
			displayFrameworkBugMessageAndExit();
		}
		/*
		 * catch (RuntimeException e) { // Barcode Scanner has seen crashes in the wild of this variety: //
		 * java.?lang.?RuntimeException: Fail to connect to camera service Log.w(TAG,
		 * "Unexpected error initializing camera", e); displayFrameworkBugMessageAndExit(); }
		 */

	}

	/**
	 * 向CaptureActivityHandler中发送消息，并展示扫描到的图像
	 * 
	 * @param bitmap
	 * @param result
	 */
	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(getResources().getString(R.string.confirm), new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.capture_scan_photo: // 图片识别
			openImgPick(this);
			break;
		case R.id.capture_flashlight:
			finish();
			overridePendingTransition(R.anim.stop, R.anim.bottom_out);
			break;
		default:
			break;
		}

	}

	public interface setOnChageEwmSize {

		public void onChageEwmSize(int topOffSet, int bottomOffSet);
	}
}
