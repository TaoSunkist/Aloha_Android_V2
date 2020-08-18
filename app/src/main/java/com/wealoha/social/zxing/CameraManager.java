/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wealoha.social.zxing;

import java.io.IOException;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.activity.CaptureActivity.setOnChageEwmSize;
import com.wealoha.social.utils.XL;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for both preview
 * and decoding. <br/>
 * <br/>
 * 
 * 该类封装了相机的所有服务并且是该app中唯一与相机打交道的类
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	// private static final int MIN_FRAME_WIDTH = 200;

	// private static final int MAX_FRAME_WIDTH = 600; // = 5/8 * 1920

	private final BaseFragAct baseFragAct;

	private final CameraConfigurationManager mConfigManager;

	private Camera mCamera;

	private AutoFocusManager autoFocusManager;

	private Rect mFramingRect;

	private Rect mFramingRectInPreview;

	private boolean initialized;

	private boolean previewing;

	private int requestedFramingRectWidth;

	private int requestedFramingRectHeight;

	/**
	 * Preview frames are delivered here, which we pass on to the registered handler. Make sure to clear the
	 * handler so it will only receive one message.
	 */
	private final PreviewCallback previewCallback;

	/**
	 * Gets the CameraManager singleton instance.
	 * 
	 * @return A reference to the CameraManager singleton.
	 */
	static CameraManager cameraManager;

	public static CameraManager get() {
		return cameraManager;
	}

	int mBarHeight;
	int mTopWrapHeight;
	private setOnChageEwmSize setOnChageEwmSize;

	public CameraManager(BaseFragAct baseFragAct) {
		this.baseFragAct = baseFragAct;
		this.mConfigManager = new CameraConfigurationManager(baseFragAct);
		previewCallback = new PreviewCallback(mConfigManager);
		cameraManager = this;
		mBarHeight = 138;
		mTopWrapHeight = baseFragAct.mScreenHeight / 2;
	}

	public CameraManager(BaseFragAct baseFragAct, setOnChageEwmSize setOnChageEwmSize) {
		this.baseFragAct = baseFragAct;
		this.mConfigManager = new CameraConfigurationManager(baseFragAct);
		previewCallback = new PreviewCallback(mConfigManager);
		cameraManager = this;
		mBarHeight = 138;
		mTopWrapHeight = baseFragAct.mScreenHeight / 2;
		this.setOnChageEwmSize = setOnChageEwmSize;
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder) throws IOException {
		long startTime = System.currentTimeMillis();
		Camera theCamera = mCamera;
		if (theCamera == null) {
			// 获取手机背面的摄像头
			theCamera = OpenCameraInterface.open();
			if (theCamera == null) {
				throw new IOException();
			}
			mCamera = theCamera;
		}
		// 设置摄像头预览view
		theCamera.setPreviewDisplay(holder);

		if (!initialized) {
			initialized = true;
			mConfigManager.initFromCameraParameters(theCamera);
			// XL.d("openDriver", requestedFramingRectWidth + "----" +
			// requestedFramingRectHeight);
			if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
				setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
				XL.d("CameraManagerCameraManager", requestedFramingRectHeight + "|||" + requestedFramingRectHeight);
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}
		}
		Camera.Parameters parameters = theCamera.getParameters();
		// List<Size> sizes= parameters.getSupportedPreviewSizes();
		String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
																						// these,
																						// temporarily
		try {
			mConfigManager.setDesiredCameraParameters(theCamera, false);
		} catch (RuntimeException re) {
			// Driver failed
			// Log.w(TAG,
			// "Camera rejected parameters. Setting only minimal safe-mode parameters");
			// Log.i(TAG, "Resetting to saved camera params: " +
			// parametersFlattened);
			// Reset:
			if (parametersFlattened != null) {
				parameters = theCamera.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					theCamera.setParameters(parameters);
					mConfigManager.setDesiredCameraParameters(theCamera, true);
				} catch (RuntimeException re2) {
					// Well, darn. Give up
					// Log.w(TAG,
					// "Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}
		long endTime = System.currentTimeMillis();
		XL.d("openDriver", (endTime - startTime) + "");
	}

	public synchronized boolean isOpen() {
		return mCamera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
			mFramingRect = null;
			mFramingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public synchronized void startPreview() {
		// long startTime = System.currentTimeMillis();
		Camera theCamera = mCamera;
		if (theCamera != null && !previewing) {
			// Starts capturing and drawing preview frames to the screen
			// Preview will not actually start until a surface is supplied with
			// setPreviewDisplay(SurfaceHolder) or
			// setPreviewTexture(SurfaceTexture).
			theCamera.startPreview();

			previewing = true;
			autoFocusManager = new AutoFocusManager(baseFragAct, mCamera);
		}
		// long endTime = System.currentTimeMillis();
		// Log.d("startPreview", (endTime - startTime) + "");
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (mCamera != null && previewing) {
			mCamera.stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * Convenience method for {@link com.wealoha.social.activity.madmatrix.zxing.android.CaptureActivity}
	 */
	public synchronized void setTorch(boolean newSetting) {
		if (newSetting != mConfigManager.getTorchState(mCamera)) {
			if (mCamera != null) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				mConfigManager.setTorch(mCamera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data will arrive as byte[] in the
	 * message.obj field, with width and height encoded as message.arg1 and message.arg2, respectively. <br/>
	 * 
	 * 两个绑定操作：<br/>
	 * 1：将handler与回调函数绑定；<br/>
	 * 2：将相机与回调函数绑定<br/>
	 * 综上，该函数的作用是当相机的预览界面准备就绪后就会调用hander向其发送传入的message
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = mCamera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);

			// 绑定相机回调函数，当预览界面准备就绪后会回调Camera.PreviewCallback.onPreviewFrame
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 600;
	private static final int MAX_FRAME_HEIGHT = 600;
	/** 二维码扫描选取框宽高占屏幕宽高的缩放比例 */
	private static final float SCALE_PREVIEW_FRAME = 0.60f;

	private int topOffset;

	private int bottomOffSet;
	boolean isSize = true;

	/**
	 * Calculates the framing rect which the UI should draw to show the user where to place the barcode. This
	 * target helps with alignment as well as forces the user to hold the device far enough away to ensure the
	 * image will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public synchronized Rect getFramingRect() {
		if (mFramingRect == null || mFramingRect.isEmpty()) {
			if (mCamera == null) {
				return null;
			}
			Point screenResolution = mConfigManager.getScreenResolution();
			if (screenResolution == null) {
				// Called early, before init even finished
				return null;
			}

			int height = (int) (screenResolution.y * SCALE_PREVIEW_FRAME);
			if (height < MIN_FRAME_HEIGHT) {
				height = MIN_FRAME_HEIGHT;
			} else if (height > MAX_FRAME_HEIGHT) {
				height = MAX_FRAME_HEIGHT;
			}

			int width = (int) (screenResolution.x * SCALE_PREVIEW_FRAME);
			width = Math.min(width, height);
			if (width < MIN_FRAME_WIDTH) {
				width = MIN_FRAME_WIDTH;
			} else if (width > MAX_FRAME_WIDTH) {
				width = MAX_FRAME_WIDTH;
			}
			topOffset = mBarHeight;
			bottomOffSet = baseFragAct.mScreenHeight / 2 - mBarHeight;
			// 计算底部剩余的高度
			int padBottom = (baseFragAct.mScreenHeight / 2 - bottomOffSet) / 2;
			topOffset = topOffset + padBottom;
			bottomOffSet = bottomOffSet + padBottom;
			int leftOffset = (screenResolution.x - width) / 2;
			int viewFinderHeight = (baseFragAct.mScreenHeight / 2 - topOffset) - (baseFragAct.mScreenHeight / 2 - bottomOffSet);
			leftOffset = (baseFragAct.mScreenWidth - viewFinderHeight) / 2;
			// mFramingRect = new Rect(leftOffset, topOffset, leftOffset +
			// width, topOffset + height);
			// mFramingRect = new Rect(50, 50, screenResolution.x / 2,
			// screenResolution.y / 2);
			// XL.d("CameraManager", mTopWrapHeight + "---" +
			// baseFragAct.mScreenHeight);
			// mFramingRect = new Rect(leftOffset, topOffset / 3, leftOffset +
			// width, (int) (topOffset + height / 3.3));
			mFramingRect = new Rect(leftOffset, topOffset, leftOffset + viewFinderHeight, bottomOffSet);
			// XL.d("mFramingRect", mFramingRect + "mFramingRect:" +
			// screenResolution.x + "/" + screenResolution.y);

		}
		if (setOnChageEwmSize != null)
			setOnChageEwmSize.onChageEwmSize(topOffset, bottomOffSet);
		// XL.d("getFramingRect", 2 + "getFramingRect:" +
		// cameraManager.getFramingRect());
		return mFramingRect;
		// if (framingRect == null) {
		// if (camera == null) {
		// return null;
		// }
		// Point screenResolution = configManager.getScreenResolution();
		// if (screenResolution == null) {
		// // Called early, before init even finished
		// return null;
		// }
		//
		// int width = findDesiredDimensionInRange(screenResolution.x,
		// MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
		// // 将扫描框设置成一个正方形
		// int height = width;
		//
		// int leftOffset = (screenResolution.x - width) / 2;
		// int topOffset = (int) ((screenResolution.y - height) / 1.5);
		// framingRect = new Rect(leftOffset, topOffset / 2, leftOffset + width,
		// topOffset + height / 2);
		//
		// Log.d(TAG, "Calculated framing rect: " + framingRect);
		// }

		// return framingRect;
	}

	Rect getCameraSelectArea() {
		Rect rect = null;

		if (mFramingRect == null || mFramingRect.isEmpty()) {
			Point screenResolution = mConfigManager.getScreenResolution();
			if (screenResolution == null) {
				return null;
			}

			int height = (int) (screenResolution.y * SCALE_PREVIEW_FRAME);
			if (height < MIN_FRAME_HEIGHT) {
				height = MIN_FRAME_HEIGHT;
			} else if (height > MAX_FRAME_HEIGHT) {
				height = MAX_FRAME_HEIGHT;
			}

			int width = (int) (screenResolution.x * SCALE_PREVIEW_FRAME);
			width = Math.min(width, height);
			if (width < MIN_FRAME_WIDTH) {
				width = MIN_FRAME_WIDTH;
			} else if (width > MAX_FRAME_WIDTH) {
				width = MAX_FRAME_WIDTH;
			}

			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			rect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
		}
		return rect;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview frame, not UI / screen.
	 * 
	 * @return 扫描二维码选取框区域
	 */
	public synchronized Rect getFramingRectInPreview() {

		if (mFramingRectInPreview == null) {
			Rect framingRect = getCameraSelectArea();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = mConfigManager.getCameraResolution();
			Point screenResolution = mConfigManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			rect.left = rect.left * cameraResolution.x / screenResolution.x;
			rect.right = rect.right * cameraResolution.x / screenResolution.x;
			rect.top = rect.top * cameraResolution.y / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
			mFramingRectInPreview = rect;
		}
		return mFramingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions, rather than determine them
	 * automatically based on screen resolution.
	 * 
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (initialized) {
			Point screenResolution = mConfigManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + mFramingRect);
			mFramingRectInPreview = null;
		} else {
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on the format of the preview
	 * buffers, as described by Camera.Parameters.
	 * 
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
		// 912/1216
		// Rect rect = getFramingRectInPreview();
		Rect rect = new Rect(0, 0, width, height);
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
	}

	/**
	 * 焦点放小
	 */
	public void zoomOut() {
		if (mCamera != null && mCamera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters.getZoom() <= 0) {
				return;
			}

			parameters.setZoom(parameters.getZoom() - 1);
			mCamera.setParameters(parameters);

		}
	}

	/**
	 * 焦点放大
	 */
	public void zoomIn() {
		if (mCamera != null && mCamera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters.getZoom() >= parameters.getMaxZoom()) {
				return;
			}

			parameters.setZoom(parameters.getZoom() + 1);
			mCamera.setParameters(parameters);

		}
	}

	/*
	 * 缩放
	 * 
	 * @param scale
	 */
	public void setCameraZoom(int scale) {
		if (mCamera != null && mCamera.getParameters().isZoomSupported() && scale <= mCamera.getParameters().getMaxZoom() && scale >= 0) {

			Camera.Parameters parameters = mCamera.getParameters();

			parameters.setZoom(scale);
			mCamera.setParameters(parameters);

		}
	}

	public int getTopOffset() {
		return topOffset;
	}

	public int getBottomOffSet() {
		return bottomOffSet;
	}

}
