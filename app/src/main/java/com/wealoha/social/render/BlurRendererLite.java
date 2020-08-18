package com.wealoha.social.render;

import javax.inject.Inject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.wealoha.social.inject.Injector;

/**
 * Blur渲染器
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 上午10:25:43
 */
public class BlurRendererLite {

	@Inject
	Context context;

	public BlurRendererLite() {
		Injector.inject(this);
	}

	/**
	 * 
	 * @param bitmap
	 * @param radius
	 *            0 < r <= 25
	 * @param recycleBitmap
	 * @return
	 * https://gist.github.com/Mariuxtheone/903c35b4927c0df18cf8
	 */
	public Bitmap blurBitmap(Bitmap bitmap, float radius, boolean recycleBitmap) {

		// Let's create an empty bitmap with the same size of the bitmap we want
		// to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		// Instantiate a new Renderscript
		RenderScript rs = null;
		try {// Cannot load library
			rs = RenderScript.create(context);
		} catch (Throwable e) {
			System.out.println("The load problem");
		}
		if (rs != null) {
			// Create an Intrinsic Blur Script using the Renderscript
			ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

			// Create the in/out Allocations with the Renderscript and the
			// in/out
			// bitmaps
			Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
			Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

			// Set the radius of the blur
			blurScript.setRadius(radius);

			// Perform the Renderscript
			blurScript.setInput(allIn);
			blurScript.forEach(allOut);

			// Copy the final bitmap created by the out Allocation to the
			// outBitmap
			allOut.copyTo(outBitmap);

			// recycle the original bitmap
			if (recycleBitmap) {
				bitmap.recycle();
			}

			// After finishing everything, we destroy the Renderscript.
			rs.destroy();

			return outBitmap;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param bmp
	 *            input bitmap
	 * @param contrast
	 *            0..10 1 is default
	 * @param brightness
	 *            -255..255 0 is default
	 * @return new bitmap
	 * @param recycleBitmap
	 *            销毁原图
	 *
	 *      ://stackoverflow.com/questions/12891520/how-to-programmatically-
	 *      change-contrast-of-a-bitmap-in- android
	 */
	public Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness, boolean recycleBitmap) {
		ColorMatrix cm = new ColorMatrix(new float[] { contrast, 0, 0, 0, brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast, 0, brightness, 0, 0, 0, 1, 0 });

		Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

		Canvas canvas = new Canvas(ret);

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		canvas.drawBitmap(bmp, 0, 0, paint);

		return ret;
	}

}
