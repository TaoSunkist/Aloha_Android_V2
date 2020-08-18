package com.wealoha.social.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wealoha.social.R;
import com.wealoha.social.utils.UiUtils;

public class SuperRangerBar extends View {

	private int height;
	private int width;
	private boolean isEnable = true;
	private Paint mBaseLinePaint;
	private Paint mCoverLinePaint;
	private Paint mThumbPaint;
	private RectF mBaseLine;
	private RectF mCoverLine;

	private int storkWidth;
	private float tempX;
	private float padding;
	private float coverLineLeft;
	private float coverLineRight;
	private float initCoverLineLeft;
	private float initCoverLineRight;
	private float lineRectfTop;
	private float lineRectfBottom;
	private float lineRadius;
	private float thumbY;
	private int sepDistScale;
	private float minSepDist;
	private float sepDist;
	private int leftScale;
	private int rightScale;

	private Bitmap mLeftThumb;
	private Bitmap mRightThumb;
	private Matrix mLeftThumbMatrix;
	private Matrix mRightThumbMatrix;

	private OnMoveListener moveListener;

	private static final int RIGHT_THUMB = 0X0001;
	private static final int LEFT_THUMB = 0X0002;
	private static final int NO_THUMB = 0X0003;
	private int whichThumb = NO_THUMB;

	public SuperRangerBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SuperRangerBar(Context context) {
		super(context);
	}

	public SuperRangerBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		mCoverLinePaint = new Paint();
		mCoverLinePaint.setColor(context.getResources().getColor(R.color.light_red));
		mCoverLinePaint.setStrokeWidth(storkWidth);

		mBaseLinePaint = new Paint();
		mBaseLinePaint.setColor(context.getResources().getColor(R.color.light_gray));
		mBaseLinePaint.setStrokeWidth(storkWidth);
		mBaseLine = new RectF();
		mCoverLine = new RectF();

		mThumbPaint = new Paint();

		mLeftThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.rangeslider_button);
		mRightThumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.rangeslider_button);

		mLeftThumbMatrix = new Matrix();
		mRightThumbMatrix = new Matrix();

		storkWidth = UiUtils.dip2px(context, 1);
		padding = 30f;
		sepDistScale = 42;
		leftScale = 0;
		rightScale = sepDistScale;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mCoverLine.set(coverLineLeft, lineRectfTop, coverLineRight, lineRectfBottom);
		mBaseLine.set(padding, lineRectfTop, width - padding, lineRectfBottom);
		mLeftThumbMatrix.setTranslate(coverLineLeft - 0.2f * mLeftThumb.getWidth(), thumbY);
		mRightThumbMatrix.setTranslate(coverLineRight - mRightThumb.getHeight() + 0.2f * mLeftThumb.getWidth(), thumbY);

		canvas.drawRoundRect(mBaseLine, lineRadius, lineRadius, mBaseLinePaint);
		canvas.drawRoundRect(mCoverLine, lineRadius, lineRadius, mCoverLinePaint);
		canvas.drawBitmap(mLeftThumb, mLeftThumbMatrix, mThumbPaint);
		canvas.drawBitmap(mRightThumb, mRightThumbMatrix, mThumbPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnable) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (whichThumb == NO_THUMB) {
				whichThumb = checkTouch(event);
			}
			calculateThumbCoordinate(event);
			invalidate();
			break;
		case MotionEvent.ACTION_DOWN:
			tempX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			tempX = 0;
			coverLineLeft = mCoverLine.left;
			coverLineRight = mCoverLine.right;
			whichThumb = NO_THUMB;
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		isEnable = enabled;
	}

	/**
	 * 检测触碰点是否在thumb控件的范围内
	 * 
	 * @param event
	 * @return {@link #LEFT_THUMB}左侧thumb，{@link #RIGHT_THUMB}右侧thumb
	 */
	private int checkTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (y < mCoverLine.bottom + mLeftThumb.getHeight() / 2 && y > mCoverLine.top - mLeftThumb.getHeight() / 2) {
			if (x < mCoverLine.left + mLeftThumb.getWidth() && x > mCoverLine.left) {
				return LEFT_THUMB;
			} else if (x < mCoverLine.right && x > mCoverLine.right - mRightThumb.getWidth()) {
				return RIGHT_THUMB;
			}
		}
		return NO_THUMB;
	}

	/**
	 * 计算thumb移动后的坐标
	 * 
	 * @param event
	 * @return void
	 */
	private void calculateThumbCoordinate(MotionEvent event) {
		float tempdis;
		float calculatedDis = calculateDis(event);
		switch (whichThumb) {
		case LEFT_THUMB:
			tempdis = coverLineLeft;
			tempdis += calculatedDis;
			// 边界检测
			if (tempdis < padding) {
				coverLineLeft = padding;
			} else if (tempdis + mLeftThumb.getWidth() + minSepDist < coverLineRight - mRightThumb.getWidth()) {
				coverLineLeft = tempdis;
			}
			break;
		case RIGHT_THUMB:
			tempdis = coverLineRight;
			tempdis += calculatedDis;
			// 边界检测
			if (tempdis > width - padding) {
				coverLineRight = width - padding;
			} else if (tempdis - mRightThumb.getWidth() - minSepDist > coverLineLeft + mLeftThumb.getWidth()) {
				coverLineRight = tempdis;
			}
			break;
		default:
			break;
		}
		callback();
		// 控制最大刷新频率
		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * 计算每次ontouch 中move事件被调用前后手指的x轴位移
	 * 
	 * @param event
	 * @return float
	 */
	private float calculateDis(MotionEvent event) {
		float dis = event.getX() - tempX;
		tempX = event.getX();
		return dis;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		initValue(getWidth(), getHeight());
	}

	private void initValue(int viewWidth, int viewHeight) {
		height = viewHeight;
		width = viewWidth;

		initCoverLineLeft = padding;
		initCoverLineRight = width - padding;

		sepDist = (initCoverLineRight - mRightThumb.getWidth()) - (initCoverLineLeft + mLeftThumb.getWidth());
		minSepDist = sepDist / sepDistScale;

		coverLineLeft = initCoverLineLeft + leftScale * minSepDist;
		coverLineRight = initCoverLineLeft + rightScale * minSepDist + mLeftThumb.getWidth() + mRightThumb.getWidth();

		lineRectfTop = (height - storkWidth) / 2;
		lineRectfBottom = (height + storkWidth) / 2;
		thumbY = (height - mLeftThumb.getHeight()) / 2;
		lineRadius = storkWidth / 2;

	}

	/**
	 * 计算thumb对应的值，并回调给这个控件的父容器
	 * 
	 * @return void
	 */
	private void callback() {
		float ltScale = ((coverLineLeft + mLeftThumb.getWidth()) - (initCoverLineLeft + mLeftThumb.getWidth())) / minSepDist;
		float rtScale = ((coverLineRight - mRightThumb.getWidth()) - (initCoverLineLeft + mLeftThumb.getWidth())) / minSepDist;

		leftScale = Math.round(ltScale);
		rightScale = Math.round(rtScale);
		if (moveListener != null) {
			moveListener.moveScale(leftScale, rightScale);
		}
	}

	/**
	 * 设置初始的选择范围以及两侧thumb所对应的初始值
	 * 
	 * @param count
	 *            控件可选的最大范围
	 * @param ltScale
	 *            左侧thumb对应的值
	 * @param rtScale
	 *            右侧thumb对应的值
	 * @return void
	 */
	public void setScale(int count, int ltScale, int rtScale) {
		if (ltScale < 0 || rtScale > count || ltScale >= rtScale) {
			return;
		}
		sepDistScale = count;
		leftScale = ltScale;
		rightScale = rtScale;
	}

	public void changeScale(int count, int ltScale, int rtScale) {
		setScale(count, ltScale, rtScale);
		invalidate();
	}

	public void setOnMoveListener(OnMoveListener moveListener) {
		this.moveListener = moveListener;
	}

	public interface OnMoveListener {

		void moveScale(int leftScale, int rightScale);
	}
}
