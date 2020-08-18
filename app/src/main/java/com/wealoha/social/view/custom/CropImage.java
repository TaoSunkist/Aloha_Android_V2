package com.wealoha.social.view.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.Dimension;
import com.wealoha.social.utils.ImageUtil.ExifOrientation;
import com.wealoha.social.utils.XL;

/**
 * 裁剪处理
 * 
 */
public class CropImage {

	// public static File FILE_LOCAL = new
	// File(GlobalConstants.AppConstact.FILE_PIC_SCREENSHOT.getAbsoluteFile(),
	// System.currentTimeMillis() +
	// GlobalConstants.AppConstact.UPLOAD_FEED_PIC);
	public static File FILE_LOCAL = new File(GlobalConstants.AppConstact.FILE_PIC_SCREENSHOT.getAbsoluteFile(), GlobalConstants.AppConstact.UPLOAD_FEED_PIC);
	public boolean mWaitingToPick; // Whether we are wait the user to pick a
									// face.
	public boolean mSaving; // Whether the "save" button is already clicked.
	public HighlightView mCrop;

	private String TAG = "CropImageActivity";

	private Context mContext;
	private Handler mHandler;
	private CropImageView mImageView;
	private Bitmap mBitmap;
	private String mOrigImageFilePath;
	private ExifOrientation mExifOrientation;
	private float mThumbRatio;

	public CropImage(Context context, CropImageView imageView, Handler handler) {
		mContext = context;
		mImageView = imageView;
		mImageView.setCropImage(this);
		mHandler = handler;
	}

	/**
	 * 
	 * @param context
	 * @param imageView
	 * @param handler
	 * @param thumbBitmap
	 *            需要裁切的小图
	 * @param origImageFilePath
	 *            原图路径
	 */
	public CropImage(Context context, CropImageView imageView, Handler handler, Bitmap thumbBitmap, String origImageFilePath, float thumbRatio, ExifOrientation exifOrientation) {
		mContext = context;
		mImageView = imageView;
		mImageView.setCropImage(this);
		mHandler = handler;
		mBitmap = thumbBitmap;
		mOrigImageFilePath = origImageFilePath;
		mExifOrientation = exifOrientation;
		mThumbRatio = thumbRatio;
		startFaceDetection();
	}

	/**
	 * 设置用来裁剪的图片
	 */
	public void crop(Bitmap bm) {
		mBitmap = bm;
		startFaceDetection();
	}

	public void startRotate(float d) {
		if (((Activity) mContext).isFinishing()) {
			return;
		}
		final float degrees = d;
		showProgressDialog(mContext.getString(R.string.please_wait), new Runnable() {

			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				mHandler.post(new Runnable() {

					public void run() {
						try {
							Matrix m = new Matrix();
							m.setRotate(degrees);
							Bitmap tb = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);
							mBitmap = tb;
							mImageView.resetView(tb);
							if (mImageView.mHighlightViews.size() > 0) {
								mCrop = mImageView.mHighlightViews.get(0);
								mCrop.setFocus(true);
							}
						} catch (Exception e) {
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				// mRunFaceDetection.run();
			}
		}, mHandler);
	}

	private void startFaceDetection() {
		// TODO 把正常逻辑从人脸识别中抽离，会影响选取框延迟出现！！
		if (((Activity) mContext).isFinishing()) {
			return;
		}
		showProgressDialog(mContext.getString(R.string.please_wait), new Runnable() {

			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				final Bitmap b = mBitmap;
				mHandler.post(new Runnable() {

					public void run() {
						if (b != mBitmap && b != null) {
							mImageView.setImageBitmapResetBase(b, true);
							mBitmap.recycle();
							mBitmap = b;
						}
						if (mImageView.getScale() == 1.0f) {
							mImageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				mRunFaceDetection.run();
			}
		}, mHandler);
	}

	/**
	 * 从原图裁切
	 * 
	 * @param expectSize
	 *            期望的尺寸
	 * @return
	 */
	public Bitmap getCropImageFromOrig(int expectSize) {

		if (mOrigImageFilePath == null) {
			throw new IllegalStateException("只有使用传入了原图的构造函数才能调用这个方法！");
		}

		// 计算采样和选区
		Rect r = mCrop.getCropRect();

		// 放回原图比例
		int x1 = (int) Math.ceil(r.left / mThumbRatio);
		int y1 = (int) Math.ceil(r.top / mThumbRatio);
		int x2 = (int) Math.ceil(r.right / mThumbRatio);
		int y2 = (int) Math.ceil(r.bottom / mThumbRatio);

		int w = x2 - x1;
		int h = y2 - y1;

		ExifOrientation orien = mExifOrientation;
		boolean doExif = false;
		XL.d(TAG, "旋转信息: " + orien == null ? "" : (orien.flip + " " + orien.angle));
		if (orien != null && (orien.flip || orien.angle != 0)) {
			doExif = true;
			XL.d(TAG, "需要转换选取坐标");
			Dimension rect = ImageUtil.getImageSize(mOrigImageFilePath);
			Point p1 = ImageUtil.reverseTranslateAndFlip(new Point(x1, y1), rect, orien);
			Point p2 = ImageUtil.reverseTranslateAndFlip(new Point(x2, y2), rect, orien);
			x1 = Math.min(p1.x, p2.x);
			x2 = Math.max(p1.x, p2.x);
			y1 = Math.min(p1.y, p2.y);
			y2 = Math.max(p1.y, p2.y);
		}

		// 和ImgCropingActivity一样，找到最合适的大小
		// 最终截取的大小不会超过expectSize*2
		int rr = w / expectSize;
		int sampleSize = 1;
		while (sampleSize <= rr) {
			sampleSize *= 2;
		}
		sampleSize = Math.max(1, sampleSize / 2);

		XL.d(TAG, "裁切范围: " + r.left + "x" + r.top + " -> " + r.right + "x" + r.bottom + "(crop thumb) -> " //
			+ x1 + "x" + y1 + "x" + x2 + "x" + y2 + "(crop orig) sample=" + sampleSize + ", cropsize=" + w + "x" + h);

		Bitmap bitmap = null;
		try {
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(mOrigImageFilePath, true);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			try {
				bitmap = decoder.decodeRegion(new Rect(x1, y1, x2, y2), options);
			} catch (OutOfMemoryError e) {
				// 如果崩溃了，尝试取更小尺寸的
				XL.w(TAG, "OOM，缩小点儿再裁切", e);
				sampleSize = Math.max(1, sampleSize / 2);
				options.inSampleSize = sampleSize;
				bitmap = decoder.decodeRegion(new Rect(x1, y1, x2, y2), options);
			}
			if (bitmap.getWidth() > expectSize) {
				// 需要缩小
				XL.d(TAG, "缩小图片到约定大小: w=" + bitmap.getWidth() + " expect w=" + expectSize);
				try {
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, expectSize, expectSize, false);
					if (scaledBitmap != bitmap) {
						bitmap.recycle();
					}
					bitmap = scaledBitmap;
				} catch (OutOfMemoryError e) {
					expectSize = Math.max(ImageSize.MIN_FEED_SIZE, Math.min(ImageSize.MAX_FEED_SIZE_IF_OOM, bitmap.getWidth() / 2));
					XL.w(TAG, "OOM，缩小点儿再裁切: " + expectSize, e);
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, expectSize, expectSize, false);
					if (scaledBitmap != bitmap) {
						bitmap.recycle();
					}
					bitmap = scaledBitmap;
				}
			}
			if (doExif) {
				XL.d(TAG, "旋转裁切后的图");
				Bitmap scaledBitmap = ImageUtil.rotateBitmap(bitmap, orien);
				if (scaledBitmap != bitmap) {
					bitmap.recycle();
				}
				bitmap = scaledBitmap;
			}
		} catch (IOException e) {
			XL.w(TAG, "io错误: " + mOrigImageFilePath, e);
			return null;
		}

		mImageView.mHighlightViews.clear();
		return bitmap;
	}

	/**
	 * 裁剪并保存
	 * 
	 * @return
	 */
	public Bitmap cropAndSave() {
		if (mSaving)
			return mBitmap;

		if (mCrop == null) {
			return mBitmap;
		}

		mSaving = true;
		Rect r = mCrop.getCropRect();
		int width = 1242;
		int height = 1242;
		// if (width > 1242 || height > 1242) {
		//
		// } else if (width < 1242 || height < 1242) {
		//
		// }
		// If we are circle cropping, we want alpha channel, which is the
		// third param here.
		Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		{
			Canvas canvas = new Canvas(croppedImage);
			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(mBitmap, r, dstRect, null);
		}
		mImageView.mHighlightViews.clear();
		return croppedImage;

	}

	// /**
	// * 裁剪并保存
	// *
	// * @return
	// */
	// public Bitmap cropAndSave(Bitmap bm) {
	// final Bitmap bmp = onSaveClicked(bm);
	// mImageView.mHighlightViews.clear();
	// return bmp;
	// }

	/**
	 * 取消裁剪
	 */
	public void cropCancel() {
		mImageView.mHighlightViews.clear();
		mImageView.invalidate();
	}

	public String saveToLocal(Bitmap bm, User user) {
		String path = FileTools.getFileImgNameHasDir(user);
		// 图片统一写入到该文件夹,并以当前的userid+当前的时间戳命名;
		FileOutputStream fos = null;
		/* 修改用户头像的时间戳 */
		try {
			fos = new FileOutputStream(path);
			bm.compress(CompressFormat.JPEG, 90, fos);
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return path;
	}

	private void showProgressDialog(String msg, Runnable job, Handler handler) {
		// final ProgressDialog progress = ProgressDialog.show(mContext, null,
		// msg);
		// new Thread(new BackgroundJob(progress, job, handler)).start();
		new Thread(new BackgroundJob(msg, job, handler)).start();
	}

	Runnable mRunFaceDetection = new Runnable() {

		float mScale = 1F;
		Matrix mImageMatrix;
		// FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// For each face, we create a HightlightView for it.
		// private void handleFace(FaceDetector.Face f) {
		// PointF midPoint = new PointF();
		//
		// int r = ((int) (f.eyesDistance() * mScale)) * 2;
		// f.getMidPoint(midPoint);
		// midPoint.x *= mScale;
		// midPoint.y *= mScale;
		//
		// int midX = (int) midPoint.x;
		// int midY = (int) midPoint.y;
		//
		// HighlightView hv = new HighlightView(mImageView);
		//
		// int width = mBitmap.getWidth();
		// int height = mBitmap.getHeight();
		//
		// Rect imageRect = new Rect(0, 0, width, height);
		//
		// RectF faceRect = new RectF(midX, midY, midX, midY);
		// faceRect.inset(-r, -r);
		// if (faceRect.left < 0) {
		// faceRect.inset(-faceRect.left, -faceRect.left);
		// }
		//
		// if (faceRect.top < 0) {
		// faceRect.inset(-faceRect.top, -faceRect.top);
		// }
		//
		// if (faceRect.right > imageRect.right) {
		// faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
		// }
		//
		// if (faceRect.bottom > imageRect.bottom) {
		// faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
		// }
		//
		// hv.setup(mImageMatrix, imageRect, faceRect, false, true);
		//
		// mImageView.add(hv);
		// }

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {
			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// CR: sentences!
			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height); // 改成默认选全部的 * 4 / 5;
			int cropHeight = cropWidth;

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(mImageMatrix, imageRect, cropRect, false, true);
			mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {
			if (mBitmap == null) {
				return null;
			}

			// // 256 pixels wide is enough.
			// if (mBitmap.getWidth() > 256) {
			// mScale = 256.0F / mBitmap.getWidth(); // CR: F => f (or change
			// // all f to F).
			// }
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			return faceBitmap;
		}

		public void run() {
			mImageMatrix = mImageView.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			// 为了快速显示裁剪狂，去除了人脸识别
			// if (faceBitmap != null) {
			// FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
			// faceBitmap.getHeight(), mFaces.length);
			// mNumFaces = detector.findFaces(faceBitmap, mFaces);
			// }

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {

				public void run() {
					mWaitingToPick = mNumFaces > 1;
					// if (mNumFaces > 0) {
					// // for (int i = 0; i < mNumFaces; i++) {
					// for (int i = 0; i < 1; i++) {
					// handleFace(mFaces[i]);
					// }
					// } else {
					makeDefault();
					// }
					mImageView.invalidate();
					if (mImageView.mHighlightViews.size() > 0) {
						mCrop = mImageView.mHighlightViews.get(0);
						mCrop.setFocus(true);
					}

					if (mNumFaces > 1) {
						// CR: no need for the variable t. just do
						// Toast.makeText(...).show().
						// Toast t = Toast.makeText(mContext,
						// R.string.multiface_crop_help,
						// Toast.LENGTH_SHORT);
						// t.show();
					}
				}
			});
		}
	};

	class BackgroundJob implements Runnable {

		// private ProgressDialog mProgress;
		// private String message;
		private Runnable mJob;
		private Handler mHandler;

		// public BackgroundJob(ProgressDialog progress, Runnable job, Handler
		// handler)
		// {
		// mProgress = progress;
		// mJob = job;
		// mHandler = handler;
		// }
		public BackgroundJob(String m, Runnable job, Handler handler) {
			// message = m;
			mJob = job;
			mHandler = handler;
			// mProgress = new ProgressDialog(mContext);
			// mProgress.setMessage(message);
			// mProgress.show();
		}

		public void run() {
			final CountDownLatch latch = new CountDownLatch(1);
			mHandler.post(new Runnable() {

				public void run() {
					try {
						mHandler.sendMessage(mHandler.obtainMessage(GlobalConstants.AppConstact.SHOW_PROGRESS));
						// if (mProgress != null && !mProgress.isShowing())
						// {
						// mProgress.show();
						// }
					} catch (Exception e) {
						XL.e("CropImage", e.getMessage());
					}

					latch.countDown();
				}
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				mJob.run();
			} finally {
				mHandler.sendMessage(mHandler.obtainMessage(GlobalConstants.AppConstact.REMOVE_PROGRESS));
				// mHandler.post(new Runnable()
				// {
				// public void run()
				// {
				// if (mProgress != null && mProgress.isShowing())
				// {
				// mProgress.dismiss();
				// mProgress = null;
				// }
				// }
				// });
			}
		}
	}
}
