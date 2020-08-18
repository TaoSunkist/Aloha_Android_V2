package com.wealoha.social.view.custom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.wealoha.social.R;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;

public class LockView extends SurfaceView implements Callback {

	/** 被选中的点 */
	private List<LockPoint> enablePointList = new ArrayList<LockView.LockPoint>();
	private List<LockPoint> pointList = new ArrayList<LockView.LockPoint>();
	private Bitmap[] pointBitmaps;

	private SurfaceHolder mHolder;
	private Bitmap defaultPointBitmap;
	private Bitmap correctPointBitmap;
	private Bitmap errorPointBitmap;
	private Paint linePaint;

	/** 控件宽度 */
	private float width;
	private float gridWidth;
	private int thumbWidth;
	private int correctColor;
	private int errorColor;

	private boolean isEnable = true;

	private LockViewListener callback;

	public LockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);

		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);

		correctColor = getResources().getColor(R.color.blue_03a9f4);
		errorColor = getResources().getColor(R.color.light_red);

		pointBitmaps = new Bitmap[9];
		linePaint = new Paint();
		linePaint.setColor(correctColor);
		linePaint.setAlpha(150);
		linePaint.setStrokeWidth(UiUtils.dip2px(context, 1));
		

		defaultPointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scrren_lock_point_background);
		correctPointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scrren_lock_point_select);
		errorPointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.screen_lock_pint_highlight_background);
		for (int i = 0; i < pointBitmaps.length; i++) {
			pointBitmaps[i] = defaultPointBitmap;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());// 正方形
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnable) {
			return false;
		}
		if(callback != null){
			callback.startCreateGestureLock();
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initPointList();
			linePaint.setColor(correctColor);
			enablePointList.clear();
			break;
		case MotionEvent.ACTION_MOVE:
			drawLine(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (enablePointList.size() > 0) {
				LockPoint thumb = enablePointList.get(enablePointList.size() - 1);
				drawLine(thumb.getCenterPointX(), thumb.getCenterPointY());
			}

			if (callback != null) {
				callback.finish(creatPasswordStr());
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 装填包含原始位置和样式的lock point 的集合，注意，要在onlayout 方法后调用
	 * 
	 * @return void
	 */
	private void fillPointList() {
		if (pointList.size() > 0) {
			return;
		}
		float temp = (gridWidth - thumbWidth) / 2f;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				float left = temp + j % 3 * gridWidth;
				float top = temp + i % 3 * gridWidth;
				LockPoint lockThumb = new LockPoint(defaultPointBitmap, correctPointBitmap, errorPointBitmap, left, top, i, j);
				// lockThumb.initDraw(canvas);
				pointList.add(lockThumb);
			}
		}
	}

	private void initDraw() {
		Canvas canvas = mHolder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (LockPoint point : pointList) {
			point.initDraw(canvas);
		}
		mHolder.unlockCanvasAndPost(canvas);
	}

	/**
	 * 手指滑动过 标记点 后画线
	 * 
	 * @param x
	 *            当前触点 x 坐标
	 * @param y
	 *            当前触点 y 坐标
	 */
	private void drawLine(float x, float y) {
		fillEnablePointList(x, y);
		checkMiddlePointEnable();
		onDrawLine(enablePointList, x, y);
	}

	/**
	 * 将符合条件的点装入{@link #enablePointList}
	 * 
	 * @param x
	 * @param y
	 * @return void
	 */
	private void fillEnablePointList(float x, float y) {
		for (LockPoint thumb : pointList) {
			if (!thumb.isEnable() && thumb.checkEnable(x, y)) {
				thumb.setPointType(LockPoint.CORRECT);
				enablePointList.add(thumb);
				break;
			}
		}
	}

	/**
	 * 检查两个刚被选中的点之间有没有其他的点，如果有则把中间点插入有效点队列中
	 * 
	 * @return void
	 */
	private void checkMiddlePointEnable() {
		LockPoint currentThumb = null;
		LockPoint preThumb = null;
		LockPoint middleThumb = null;
		XL.i("CHECK_MIDDLE_POINT", "size:" + enablePointList.size());
		if (enablePointList.size() > 1) {
			int lastPosition = enablePointList.size() - 1;
			currentThumb = enablePointList.get(lastPosition);

			preThumb = enablePointList.get(enablePointList.size() - 2);

			int temp = (preThumb.getMatrixPosition() + currentThumb.getMatrixPosition()) / 2;
			// (y-y2)(x1-x2) = (x-x2)(y1-y2)两点式,判断位于thumb 集合中，两个已知thumb中间的这个thumb 在矩阵中的位置是否也在已知的两个thumb之间
			middleThumb = pointList.get(temp);
			if (middleThumb.isEnable()) {// 如果已经被选中，那么就不用继续了
				XL.i("CHECK_MIDDLE_POINT", "isEnable:" + middleThumb.isEnable());
				return;
			}
			int rightResult = (middleThumb.getMatrixY() - currentThumb.getMatrixY()) * (preThumb.getMatrixX() - currentThumb.getMatrixX());
			int leftResult = (middleThumb.getMatrixX() - currentThumb.getMatrixX()) * (preThumb.getMatrixY() - currentThumb.getMatrixY());
			if (rightResult == leftResult) {
				XL.i("CHECK_MIDDLE_POINT", "===");
				middleThumb.setPointType(LockPoint.CORRECT);
				enablePointList.add(lastPosition, middleThumb);
			}
		}
	}

	public void onDrawLine(List<LockPoint> lockPointList, float x, float y) {
		Canvas canvas = mHolder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		if (lockPointList != null) {
			for (int i = 0; i < lockPointList.size(); i++) {
				LockPoint lockThumb = lockPointList.get(i);
				LockPoint prelockThumb = null;

				if (i > 0) {
					prelockThumb = lockPointList.get(i - 1);
					canvas.drawLine(lockThumb.getCenterPointX(),//
									lockThumb.getCenterPointY(),//
									prelockThumb.getCenterPointX(),//
									prelockThumb.getCenterPointY(), linePaint);
				}

				if (i == lockPointList.size() - 1) {// 最后一个点
					if (x == -1 || y == -1) {
						continue;
					}
					lockThumb = lockPointList.get(i);
					canvas.drawLine(lockThumb.getCenterPointX(), lockThumb.getCenterPointY(), x, y, linePaint);
				}
			}
		}

		for (LockPoint thumb : pointList) {
			thumb.drawPoint(canvas);
		}
		mHolder.unlockCanvasAndPost(canvas);
	}

	public void changeToErrorState() {
		linePaint.setColor(errorColor);
		for (LockPoint point : enablePointList) {
			point.setPointType(LockPoint.ERROR);
		}
		onDrawLine(enablePointList, -1, -1);
	}

	/**
	 * 
	 * 将所有点重置为没有被选中的状态
	 * 
	 * @return void
	 */
	public void initPointList() {
		for (LockPoint point : pointList) {
			point.setPointType(LockPoint.DEFAULT);
		}
	}

	private String creatPasswordStr() {
		if (enablePointList == null) {
			return null;
		}

		XL.i("GESTURE_LOCK_PASSWORD", "enablePointList size:" + enablePointList.size());
		StringBuilder sb = new StringBuilder();
		for (LockPoint point : enablePointList) {
			sb.append(point.getMatrixPosition());
		}
		return sb.toString();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int right, int bottom) {
		super.onLayout(changed, l, t, right, bottom);
		width = getWidth();
		gridWidth = width / 3f;
		thumbWidth = defaultPointBitmap.getWidth();
	}

	public void setLockViewListener(LockViewListener listener) {
		callback = listener;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		XL.i("GESTURE_LOCK_PASSWORD", "surfaceCreated");
		// initDraw();
		// mHolder = holder;
		// clear(mHolder);
		// pointList.clear();
		fillPointList();
		initDraw();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		XL.i("GESTURE_LOCK_PASSWORD", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public class LockPoint {

		private float left;
		private float top;
		private int matrixX;
		private int matrixY;
		private int matrixPosition;

		private static final int DEFAULT = 0x0001;
		private static final int CORRECT = 0x0002;
		private static final int ERROR = 0x0003;
		private int pointType = DEFAULT;

		private Bitmap defaultPoint;
		private Bitmap correctPoint;
		private Bitmap errorPoint;
		private Paint paint;

		public LockPoint(Bitmap defaultPoint, Bitmap correctPoint, Bitmap errorPoint, float left, float top, int x, int y) {
			this.left = left;
			this.top = top;
			this.defaultPoint = defaultPoint;
			this.correctPoint = correctPoint;
			this.errorPoint = errorPoint;

			matrixX = x;
			matrixY = y;
			matrixPosition = 3 * x + y;
			paint = new Paint();
			paint.setColor(Color.GREEN);
		}

		public void initDraw(Canvas canvas) {
			canvas.drawBitmap(defaultPoint, left, top, paint);
			// canvas.drawCircle(getCenterPointX(), getCenterPointY(), 50, paint);
		}

		public void drawPoint(Canvas canvas) {
			Bitmap crrentPointBitmap = null;
			switch (pointType) {
			case DEFAULT:
				crrentPointBitmap = defaultPoint;
				break;
			case CORRECT:
				crrentPointBitmap = correctPoint;
				break;
			case ERROR:
				crrentPointBitmap = errorPoint;
				break;
			default:
				crrentPointBitmap = defaultPoint;
				break;
			}

			canvas.drawBitmap(crrentPointBitmap, left, top, paint);
			// canvas.drawCircle(getCenterPointX(), getCenterPointY(), 50, paint);
		}

		public int getMatrixPosition() {
			return matrixPosition;
		}

		public int getMatrixX() {
			return matrixX;
		}

		public int getMatrixY() {
			return matrixY;
		}

		public float getCenterPointX() {
			return defaultPoint.getWidth() / 2f + left;
		}

		public float getCenterPointY() {
			return defaultPoint.getWidth() / 2f + top;
		}

		public float[] getCenterPoint() {
			float[] point = { defaultPoint.getWidth() / 2 + left, defaultPoint.getWidth() / 2 + top };
			return point;
		}

		public boolean checkEnable(float x, float y) {
			return x > left && y > top && x < left + defaultPoint.getWidth() && y < top + defaultPoint.getWidth();
		}

		public boolean isEnable() {
			return pointType == CORRECT;
		}

		public void setPointType(int pointType) {
			this.pointType = pointType;
		}
	}

	public interface LockViewListener {

		void finish(String passwords);
		
		void startCreateGestureLock();
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}

	public boolean isEnable() {
		return isEnable;
	}
	/**
	 * 清空手势锁控件的所有样式，将控件的样式至于初始状态
	 */
	public void clearDrowed(){
		initPointList();
		onDrawLine(null, 0 ,0);
	}

}
