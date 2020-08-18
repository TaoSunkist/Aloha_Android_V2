package com.wealoha.social.zxing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.wealoha.social.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 * 
 * <br/>
 * <br/>
 * 该视图是覆盖在相机的预览视图之上的一层视图。扫描区构成原理，其实是在预览视图上画四块遮罩层， 中间留下的部分保持透明，并画上一条激光线，实际上该线条就是展示而已，与扫描功能没有任何关系。
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 60L;

	/**
	 * 画笔对象的引用
	 */
	private Paint mPaint;

	private static final int MAX_RESULT_POINTS = 20;

	private Bitmap resultBitmap;

	private List<ResultPoint> possibleResultPoints;

	/** Laser color */
	private final int mLaserColor;

	/** Frame Rectangle border */
	private final Drawable mLaserBorder;
	/**
	 * 第一次绘制控件
	 */
	boolean isFirst = true;
	/** 画聚焦提示语的Paint */
	private final Paint mTextPaint;
	private CameraManager cameraManager;
	/** 十字架的长度的一半 */
	private final int mCrossHalf;

	/** 画十字架的画笔的宽度 */
	private final int mCrossStroke;
	/** Laser line current alpha */
	private int mScannerAlpha;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 开启反锯齿

		Resources resources = getResources();

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		mPaint.setStyle(Style.STROKE);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);

		mMaskColor = resources.getColor(R.color.viewfinder_mask);
		mLaserColor = resources.getColor(R.color.viewfinder_laser);
		mLaserBorder = resources.getDrawable(R.drawable.barcode_frame_border);
		mCrossHalf = resources.getDimensionPixelSize(R.dimen.barcode_cross_length) / 2;
		mCrossStroke = resources.getDimensionPixelSize(R.dimen.barcode_cross_width);
		mScannerAlpha = 0;

		possibleResultPoints = new ArrayList<ResultPoint>(5);

	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	/** Point size */
	private static final int POINT_SIZE = 10;

	/** 红色的聚焦十字 */
	private Path mCrossPath = new Path();
	/** Framing Rectangle */
	private Rect mFrameRect = new Rect();
	/** Mask color */
	private final int mMaskColor;
	/** 扫描线的动画效果的alpha值数组 */
	private static final int[] SCANNER_ALPHA = { 0, 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 180, 160, 140, 120, 100, 80, 60, 40, 20 };

	@Override
	public void onDraw(Canvas canvas) {
		// if (cameraManager == null) {
		// return; // not ready yet, early draw before done configuring
		// }
		// Rect frame = cameraManager.getFramingRect();
		// if (frame == null) {
		// return;
		// }
		//
		// // 绘制遮掩层
		// // drawCover(canvas, frame);
		//
		// if (resultBitmap != null) { // 绘制扫描结果的图
		// // Draw the opaque result bitmap over the scanning rectangle
		// // paint.setAlpha(0xA0);
		// // canvas.drawBitmap(resultBitmap, null, frame, paint);
		// } else {
		//
		// // 获取屏幕的宽和高
		// int width = canvas.getWidth();
		// int height = canvas.getHeight();
		//
		// mPaint.setColor(resultBitmap != null ? resultColor : maskColor);
		//
		// // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		// canvas.drawRect(0, 0, width, frame.top, mPaint);
		// canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
		// canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
		// mPaint);
		// canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);
		//
		// // 绘制扫描线
		// drawScanningLine(canvas, frame);
		//
		// List<ResultPoint> currentPossible = possibleResultPoints;
		// Collection<ResultPoint> currentLast = lastPossibleResultPoints;
		// if (currentPossible.isEmpty()) {
		// lastPossibleResultPoints = null;
		// } else {
		// possibleResultPoints = new ArrayList<ResultPoint>(5);
		// lastPossibleResultPoints = currentPossible;
		// mPaint.setAlpha(OPAQUE);
		// mPaint.setColor(resultPointColor);
		// for (ResultPoint point : currentPossible) {
		// canvas.drawCircle(frame.left + point.getX(), frame.top +
		// point.getY(), 6.0f, mPaint);
		// }
		// }
		// if (currentLast != null) {
		// mPaint.setAlpha(OPAQUE / 2);
		// mPaint.setColor(resultPointColor);
		// for (ResultPoint point : currentLast) {
		// canvas.drawCircle(frame.left + point.getX(), frame.top +
		// point.getY(), 3.0f, mPaint);
		// }
		// }
		//
		// // 只刷新扫描框的内容，其他地方不刷新
		// postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
		// frame.right, frame.bottom);

		// }
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}

		final Rect frame = mFrameRect;

		if (frame.isEmpty()) {
			final Rect framingRect = cameraManager.getFramingRect();
			if (framingRect == null) {
				return;
			} else {
				// if (mSupportCameraPortrait) {
				// frame.set(framingRect.top, framingRect.left,
				// framingRect.bottom, framingRect.right);
				// } else {
				frame.set(framingRect);
				// }
			}
		}

		final int width = canvas.getWidth();
		final int height = canvas.getHeight();
		final int min = Math.min(width, height);

		// Draw the exterior (i.e. outside the framing rect) darkened
		canvas.drawColor(mMaskColor);
		canvas.save();
		canvas.clipRect(frame.left + 2, frame.top + 2, frame.right - 2, frame.bottom - 2);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
		canvas.restore();
		mPaint.setStrokeWidth(min / 200.0f);
		mPaint.setColor(Color.BLACK);
		// canvas.clipRect(0, 0, width, height);
		// canvas.drawRect(frame, mPaint);

		// Draw a red "laser scanner" line through the middle to show decoding
		// is active
		mPaint.setColor(mLaserColor);
		mPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
		mPaint.setStrokeWidth(mCrossStroke);
		mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
		if (mCrossPath.isEmpty()) {
			int halfWidth = width / 2;
			int halfHeight = (int) (height / 2);
			mCrossPath.moveTo(halfWidth - mCrossHalf, halfHeight);
			mCrossPath.lineTo(halfWidth + mCrossHalf, halfHeight);
			mCrossPath.moveTo(halfWidth, halfHeight - mCrossHalf);
			mCrossPath.lineTo(halfWidth, halfHeight + mCrossHalf);
		}
		// canvas.drawPath(mCrossPath, mPaint);

		mLaserBorder.setBounds(frame.left - POINT_SIZE, frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
		mLaserBorder.draw(canvas);

		// mTextPaint.setTextSize(15.0f * min / 200.0f);
		// float strWidth = mTextPaint.measureText(mLaserTip);
		// canvas.drawText(mLaserTip, 0, mLaserTip.length(), (width - strWidth)
		// / 2, frame.top - 12 - min / 24.0f, mTextPaint);

		// Request another update at the animation interval, but only repaint
		// the laser line, not the entire viewfinder mask.
		postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE, frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

}
