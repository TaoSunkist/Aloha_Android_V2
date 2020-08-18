package com.wealoha.social.view.custom;

import javax.inject.Inject;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.wealoha.social.R;
import com.wealoha.social.inject.Injector;

public class WaterView extends View {

	@Inject
	Context context;

	private Paint mCircle;
	private Paint mCircleDis;
	private Paint mPhotoPain;

	private float mRadiusTemp = 0;
	private float mRadius = 0;
	private float mAlpha = 0;

	private float mRadiusDisTemp = 0;
	private float mRadiusDis = 0;
	private float mAlphaDis = 0;

	private float mPhotoRadius;
	private float xc;
	private float yc;
	private int mHeight;
	private int mWidth;
	private ValueAnimator va;
	private EndListener endListener;

	public WaterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Injector.inject(this);
		int color = context.getResources().getColor(R.color.light_red);
		mCircle = new Paint();
		mCircle.setColor(color);
		mCircleDis = new Paint();
		mCircleDis.setColor(color);
		mPhotoPain = new Paint();
		mPhotoPain.setColor(color);
		mPhotoPain.setAlpha(100);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawColor(Color.BLUE);
		// Bitmap b = ((BitmapDrawable) getResources().getDrawable(R.drawable.myotee1)).getBitmap();
		// this.photo = getRoundBitmap(b, 10, 10);
		mCircle.setAlpha((int) mAlpha);
		mCircleDis.setAlpha((int) mAlphaDis);
		canvas.drawCircle(xc, xc, mRadiusTemp, mCircle);
		canvas.drawCircle(xc, xc, mRadiusDisTemp, mCircleDis);
		canvas.drawCircle(xc, xc, mPhotoRadius, mPhotoPain);
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// startLoadingAnimation();
	// }
	// return super.onTouchEvent(event);
	// }

	public void startLoadingAnimation() {
		if (va != null && va.isStarted()) {
			return;
		}
		va = ValueAnimator.ofFloat(0f, 600f);
		Log.i("WATER_ANIMATION", "start");
		va.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float f = (Float) animation.getAnimatedValue();
				if (f < 400) {
					mRadiusTemp = mPhotoRadius + mRadius * (f / 400);
					mAlpha = 255 - 255 * (f / 400);
				}
				if (f >= 200) {
					// Log.i("animatior", (Float) animation.getAnimatedValue() - 200 + ":" + f);
					mRadiusDisTemp = mPhotoRadius + mRadiusDis * (f - 200) / 400;
					mAlphaDis = 255 - 255 * (f - 200) / 400;
				}
				WaterView.this.invalidate();
				WaterView.this.requestLayout();
			}
		});
		va.setDuration(4000);
		va.setRepeatCount(1000);
		va.setRepeatMode(Animation.RESTART);
		va.start();
	}

	public void stopAnimation() {
		if (va != null && va.isStarted()) {
			va.cancel();
			ValueAnimator valueAnim01 = ValueAnimator.ofFloat(0, 255);
			valueAnim01.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (Float) animation.getAnimatedValue();
					mAlphaDis = mAlphaDis * (1 - value / 255);
					mAlpha = mAlpha * (1 - value / 255);
					WaterView.this.invalidate();
					WaterView.this.requestLayout();
				}
			});
			valueAnim01.setDuration(2500);
			valueAnim01.start();
			if (endListener != null) {
				endListener.end();
			}
			// va.setDuration(2000);
			// va.reverse();
			// va.addUpdateListener(new AnimatorUpdateListener() {
			//
			// @Override
			// public void onAnimationUpdate(ValueAnimator animation) {
			// Float f = (Float) animation.getAnimatedValue();
			// Log.i("WATER_VIEW", "water:" + f);
			// if (f < 2) {
			// va.cancel();
			// if (endListener != null) {
			// endListener.end();
			// }
			// }
			// }
			// });
		}
	}

	public boolean isEnd() {
		if (va != null) {
			return !va.isRunning();
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		xc = mWidth / 2;
		yc = mHeight / 2;
		mRadius = yc - mPhotoRadius;
		mRadiusDis = yc - mPhotoRadius;
		mPhotoRadius = 10;
	}

	public void setEndListener(EndListener endListener) {
		this.endListener = endListener;
	}

	public interface EndListener {

		public void end();
	}
}
