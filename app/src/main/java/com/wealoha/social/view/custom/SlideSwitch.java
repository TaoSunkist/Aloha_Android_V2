package com.wealoha.social.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.wealoha.social.R;

/**
 * SlideSwitch 仿iphone滑动开关组件，仿百度魔图滑动开关组件 组件分为三种状态：打开、关闭、正在滑动<br/>
 * 使用方法：
 * 
 * <pre>
 * SlideSwitch slideSwitch = new SlideSwitch(this);
 * slideSwitch.setOnSwitchChangedListener(onSwitchChangedListener);
 * linearLayout.addView(slideSwitch);
 * </pre>
 * 
 * 注：也可以加载在xml里面使用
 * 
 * @author scott
 * 
 */
public class SlideSwitch extends View {

	public static final String TAG = "SlideSwitch";
	public static final int SWITCH_OFF = 0;// 关闭状态
	public static final int SWITCH_ON = 1;// 打开状态

	// 用于显示的文本

	private int mSwitchStatus = SWITCH_OFF;

	private int mSrcX = 0, mDstX = 0;

	private int mBmpWidth = 0;
	private int mBmpHeight = 0;
	private int mThumbWidth = 0;

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private OnSwitchChangedListener mOnSwitchChangedListener = null;

	// 开关状态图
	Bitmap mSwitch_off, mSwitch_on, mSwitch_thumb;

	public SlideSwitch(Context context) {
		super(context);
	}

	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// 初始化三幅图片
	private void init() {
		Resources res = getResources();
		mSwitch_off = BitmapFactory.decodeResource(res, R.drawable.bg_switch_off);
		mSwitch_on = BitmapFactory.decodeResource(res, R.drawable.pop_toggle_trigger);
		mSwitch_thumb = BitmapFactory.decodeResource(res, R.drawable.switch_thumb);
		mBmpWidth = mSwitch_on.getWidth();
		mBmpHeight = mSwitch_on.getHeight();
		mThumbWidth = mSwitch_thumb.getWidth();
	}

	public void init(int switchOff, int switchOn, int switchThumb) {
		Resources res = getResources();
		mSwitch_off = BitmapFactory.decodeResource(res, switchOff);
		mSwitch_on = BitmapFactory.decodeResource(res, switchOn);
		mSwitch_thumb = BitmapFactory.decodeResource(res, switchThumb);
		mBmpWidth = mSwitch_on.getWidth();
		mBmpHeight = mSwitch_on.getHeight();
		mThumbWidth = mSwitch_thumb.getWidth();
	}

	@Override
	public void setLayoutParams(LayoutParams params) {
		params.width = mBmpWidth;
		params.height = mBmpHeight;
		super.setLayoutParams(params);
	}

	/**
	 * 为开关控件设置状态改变监听函数
	 * 
	 * @param onSwitchChangedListener
	 *            参见 {@link OnSwitchChangedListener}
	 */
	public void setOnSwitchChangedListener(OnSwitchChangedListener onSwitchChangedListener) {
		mOnSwitchChangedListener = onSwitchChangedListener;
	}

	/**
	 * 设置开关上面的文本
	 * 
	 * @param onText
	 *            控件打开时要显示的文本
	 * @param offText
	 *            控件关闭时要显示的文本
	 */
	public void setText(final String onText, final String offText) {
		invalidate();
	}

	/**
	 * 设置开关的状态
	 * 
	 * @param on
	 *            是否打开开关 打开为true 关闭为false
	 */
	public void setStatus(boolean on) {
		mSwitchStatus = (on ? SWITCH_ON : SWITCH_OFF);
	}

	public boolean isChecked() {
		return mSwitchStatus == SWITCH_ON;
	}

	public void setChecked(boolean flag) {
		if (flag) {
			mSwitchStatus = SWITCH_ON;

		} else {
			mSwitchStatus = SWITCH_OFF;
		}
		changeState(mSwitchStatus);
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		Log.d(TAG, "onTouchEvent  x=" + event.getX());
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mSrcX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			mDstX = Math.max((int) event.getX(), 10);
			mDstX = Math.min(mDstX, 62);
			if (mSrcX == mDstX)
				return true;
			mSrcX = mDstX;
			break;
		case MotionEvent.ACTION_UP:
			mSwitchStatus = Math.abs(mSwitchStatus - 1);
			changeState(mSwitchStatus);
			if (mOnSwitchChangedListener != null) {
				boolean flag = true;
				if (mSwitchStatus == SWITCH_ON) {
					flag = true;
				} else {
					flag = false;
				}
				mOnSwitchChangedListener.onSwitchChanged(this, flag);
			}
			break;

		default:
			break;
		}
		performClick();
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private Rect rect1 = new Rect(0, 0, mDstX, mBmpHeight);
	private Rect rect2 = new Rect(0, 0, (int) mDstX, mBmpHeight);
	private Rect rect3 = new Rect(mDstX, 0, mBmpWidth, mBmpHeight);
	private Rect rect4 = new Rect(0, 0, mBmpWidth - mDstX, mBmpHeight);

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘图的时候 内部用到了一些数值的硬编码，其实不太好，
		// 主要是考虑到图片的原因，图片周围有透明边界，所以要有一定的偏移
		// 硬编码的数值只要看懂了代码，其实可以理解其含义，可以做相应改进。
		mPaint.setTextSize(14);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);

		if (mSwitchStatus == SWITCH_OFF) {
			drawBitmap(canvas, null, null, mSwitch_off);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			mPaint.setColor(Color.rgb(105, 105, 105));
			canvas.translate(mSwitch_thumb.getWidth(), 0);
			canvas.drawText("", 0, 20, mPaint);
		} else if (mSwitchStatus == SWITCH_ON) {
			drawBitmap(canvas, null, null, mSwitch_on);
			int count = canvas.save();
			canvas.translate(mSwitch_on.getWidth() - mSwitch_thumb.getWidth(), 0);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			mPaint.setColor(Color.WHITE);
			canvas.restoreToCount(count);
			canvas.drawText("", 17, 20, mPaint);
		} else // SWITCH_SCROLING
		{
			mSwitchStatus = mDstX > 35 ? SWITCH_ON : SWITCH_OFF;
			drawBitmap(canvas, rect1, rect2, mSwitch_on);
			mPaint.setColor(Color.WHITE);
			canvas.drawText("", 17, 20, mPaint);

			int count = canvas.save();
			canvas.translate(mDstX, 0);
			drawBitmap(canvas, rect3, rect4, mSwitch_off);
			canvas.restoreToCount(count);

			count = canvas.save();
			canvas.clipRect(mDstX, 0, mBmpWidth, mBmpHeight);
			canvas.translate(mThumbWidth, 0);
			mPaint.setColor(Color.rgb(105, 105, 105));
			canvas.drawText("", 0, 20, mPaint);
			canvas.restoreToCount(count);

			count = canvas.save();
			canvas.translate(mDstX - mThumbWidth / 2, 0);
			drawBitmap(canvas, null, null, mSwitch_thumb);
			canvas.restoreToCount(count);
		}

	}

	public void drawBitmap(Canvas canvas, Rect src, Rect dst, Bitmap bitmap) {
		dst = (dst == null ? new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()) : dst);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, src, dst, paint);
	}

	private void changeState(int state) {
		invalidate();
		rect1 = new Rect(0, 0, mDstX, mBmpHeight);
		rect2 = new Rect(0, 0, (int) mDstX, mBmpHeight);
		rect3 = new Rect(mDstX, 0, mBmpWidth, mBmpHeight);
		rect4 = new Rect(0, 0, mBmpWidth - mDstX, mBmpHeight);
	}

	public static interface OnSwitchChangedListener {

		/**
		 * 状态改变 回调函数
		 * 
		 * @param status
		 *            SWITCH_ON表示打开 SWITCH_OFF表示关闭
		 */
		public abstract void onSwitchChanged(SlideSwitch obj, boolean isChecked);
	}

}