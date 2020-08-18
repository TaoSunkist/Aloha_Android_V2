package com.wealoha.social.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wealoha.social.R;

/**
* @author javamonk
* @createTime 14-9-27 下午9:08
*/
public class MatchCircleView extends View {

    private final static String TAG = "MatchCircleView";

    public MatchCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchCircleView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawFoggyWindowWithTransparentCircle(canvas, canvas.getWidth(), canvas.getHeight(),
                canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 - 20);


        super.onDraw(canvas);
    }

    /**
     *
     * @param canvas
     * @param circleX
     * @param circleY
     * @param radius
     * @see http://stackoverflow.com/questions/4159959/android-circular-gradient-alpha-mask
     */
    private void drawFoggyWindowWithTransparentCircle(Canvas canvas, int width, int height,
                                                      float circleX, float circleY, float radius) {

        // Get the "foggy window" bitmap
        Log.d(TAG, "draw circle: width=" + width + ", height=" + height
                + ", circleX=" + circleX + ", circleY=" + circleY
                + ", radius=" + radius);

        // Create a temporary bitmap
        Bitmap tempBitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.match_oval));

        tempCanvas.drawCircle(circleX, circleY, radius, p);

        // Copy foggyWindowBmp into tempBitmap
        //tempCanvas.drawBitmap(foggyWindowBmp, 0, 0, null);

        // Create a radial gradient
        RadialGradient gradient = new RadialGradient(
                circleX, circleY,
                radius, 0xFF000000, 0x22000000,
                android.graphics.Shader.TileMode.CLAMP);

        // Draw transparent circle into tempBitmap
        p = new Paint();
        p.setShader(gradient);
        p.setColor(0xFF000000);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        //tempCanvas.drawCircle(circleX, circleY, radius, p);

        // Draw tempBitmap onto the screen (over what's already there)
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }
}
