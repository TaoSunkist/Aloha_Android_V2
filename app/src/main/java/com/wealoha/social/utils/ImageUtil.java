package com.wealoha.social.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wealoha.social.R;
import com.wealoha.social.activity.CaptureActivity;
import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.User;
import com.wealoha.social.callback.CallbackImpl;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.GlobalConstants;

/**
 * 图片相关功能
 *
 * @author javamonk
 * @author sunkist
 * @date 2014-10-28 下午2:34:51
 * @see
 * @since
 */
public class ImageUtil {

    public static final String TAG = ImageUtil.class.getSimpleName();

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static final int UNCONSTRAINED = -1;

    public static enum CropMode {
        /**
         * 居中裁切
         */
        ScaleCenterCrop("ScaleCenterCrop");

        private final String value;

        private CropMode(String value) {
            this.value = value;
        }

    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;// 240,30
        double h = options.outHeight;// 215,27
        // if(w*h>maxNumOfPixels){
        // return 1;
        // }
        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 缩放图片
     *
     * @param icon
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap icon, int h) {
        // 缩放图片
        Matrix m = new Matrix();
        float sx = (float) 2 * h / icon.getWidth();
        float sy = (float) 2 * h / icon.getHeight();
        m.setScale(sx, sy);
        // 重新构造一个2h*2h的图片
        return Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), m, false);
    }

    public static Bitmap tDCIcon(Bitmap icon, int h) {
        // 缩放图片
        Bitmap logo = toRoundCorner(icon, 360);

        Matrix m = new Matrix();
        float sx = (float) 2 * h / icon.getWidth();
        float sy = (float) 2 * h / icon.getHeight();
        m.setScale(sx, sy);
        // 重新构造一个2h*2h的图片
        return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), m, false);
    }

    /**
     * @param @param  path
     * @param @param  w
     * @param @param  h
     * @param @return 设定文件
     * @return Bitmap 返回类型
     * @throws
     * @Title: createBitmap
     * @Description: 根据path得到图片并调整大小
     */
    public static Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图片
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        return bitmap;
    }

    /**
     * @param newPath 压缩后图片的路径
     * @param size    压缩比例
     * @return
     * @Description: oldPath
     * 原本图片的路径
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-13
     */
    public static Bitmap smallPic(Bitmap bitmap, String newPath, int size) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap resizeBmp = bitmap;
        opts.inSampleSize = size;
        opts.inJustDecodeBounds = false;
        if (resizeBmp == null) {
            // 如果路径和图片为空
            return null;
        } else if (TextUtils.isEmpty(newPath)) {
            // 如果路径为空

        } else {
            // 如果路径不为空
            File pictureFile = new File(newPath);
            try {
                if (pictureFile.exists()) {
                    pictureFile.delete();
                }
                pictureFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(pictureFile);
                resizeBmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (resizeBmp.isRecycled() == false) {
                resizeBmp.recycle();
                System.gc();
            }
        }

        return resizeBmp;
    }

    /**
     * 获取图片地址
     *
     * @param id
     * @param width
     * @param cropMode 可以传null，不裁切
     * @return
     */
    public static String getImageUrl(String id, int width, CropMode cropMode) {
        Image image = ImageCache.getImageFromMap(id);

        String url = null;

        if (image != null) {
            url = getImageUrl(image, width, cropMode);
        }
        String urlPrefix = GlobalConstants.ServerUrl.SERVER_URL + "/v1/file/image";
        if (url == null) {
            if (width <= 0) {
                url = urlPrefix + "?id=" + id //
                        + (cropMode != null ? "&mode=" + cropMode.value : "");
            } else {
                url = urlPrefix + "?id=" + id + "&width=" + width //
                        + (cropMode != null ? "&mode=" + cropMode.value : "");
            }
        }
        XL.d(TAG, "返回图片地址: " + url);
        return url;

    }

    public static String getImageUrl(Image image, int width, CropMode cropMode) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        String widthStr = String.valueOf(width);
        // 如果需要裁切
        if (cropMode == CropMode.ScaleCenterCrop) {
            String urlPatternWidthHeight = image.getUrlPatternWidthHeight();
            if (StringUtil.isNotEmpty(urlPatternWidthHeight)) {
                return replacePattern(urlPatternWidthHeight, widthStr, widthStr);
            }
        }
        if (StringUtil.isNotEmpty(image.getUrlPatternWidth())) {
            return replacePattern(image.getUrlPatternWidth(), widthStr);
        }
        return null;
    }

    private static String replacePattern(String target, Object... args) {
        target = target.replaceAll("%@", "%s");
        return String.format(target, args);
    }

    static public Bitmap handPic(String imgUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            if (!("".equals(imgUrl))) {
                URL url = new URL(imgUrl);
                /* 设置头像下载地址 */
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                // 获取头像失败
            }
        }
        return bitmap;
    }

    public static String saveToLocal(Bitmap bm, String fileName) {
        String path = "";
        FileOutputStream fos = null;
        path = fileName;
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
        } catch (NullPointerException e) {
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

    public static String saveToLocal(Bitmap bm, String fileName, int num) {
        String path = "";
        FileOutputStream fos = null;
        path = fileName;
        /* 修改用户头像的时间戳 */
        try {
            fos = new FileOutputStream(path);
            bm.compress(CompressFormat.JPEG, num, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
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

    public static Bitmap getScreenshot(View view) {
        // view.setDrawingCacheEnabled(true);
        // view.buildDrawingCache();

        // view.measure(View.MeasureSpec.makeMeasureSpec(0,
        // View.MeasureSpec.UNSPECIFIED),
        // View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // Log.d(TAG, "view measure width=" + view.getMeasuredWidth() +
        // ", height=" + view.getMeasuredHeight());
        // view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // Log.d(TAG, "view layout width=" + view.getLayoutParams().width +
        // ", height=" + view.getLayoutParams().height);

        try {
            // TODO 根据文档，貌似不用打开cache，直接draw就可以了
            // view.setDrawingCacheEnabled(false);
            Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            // b = Bitmap.createBitmap(view.getDrawingCache());
            // view.invalidate();
            view.draw(c);
            return b;

        } finally {
            // view.setDrawingCacheEnabled(false);
        }
    }

    // public static void saveBitmap(Bitmap bm, String picName, int scale) {
    // FileOutputStream out = null;
    // File f = new
    // File(AssignController.getInstance().getOtherFilePathWithCommon(AppApplication.getInstance()),
    // picName);
    // if (f.exists()) {
    // f.delete();
    // }
    // try {
    // out = new FileOutputStream(f);
    // bm.compress(Bitmap.CompressFormat.JPEG, scale, out);
    // out.flush();
    // out.close();
    // } catch (FileNotFoundException e) {
    // } catch (IOException e) {
    // } finally {
    // try {
    // if (out != null) {
    // out.close();
    // }
    // } catch (Exception e2) {
    // XL.d(TAG, e2.toString());
    // }
    // }
    // }

    /**
     * 把位图变成圆角位图
     *
     * @param bitmap 需要修改的位图
     * @param pixels 圆角的弧度
     * @return 圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // bitmap画到现在的bitmap
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getRoundBitmap(Bitmap src) {
        int radius = src.getWidth() / 2;
        BitmapShader bitmapShader = new BitmapShader(src, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        c.drawCircle(radius, radius, radius, paint);
        return dest;
    }

    /**
     * @param bmp
     * @param needRecycle
     * @return
     * @Description: 更改原有的转换，使用画布将图片绘制到缓冲屏幕上面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-7-22
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);// 创建屏幕大小的缓冲区
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();

            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 50, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * @param url
     * @param pixels
     * @return 圆角Drawable对象
     * @throws Exception 异常
     * @Description:
     * @see:
     * @since:
     * @description 从路径中得到圆角Drawable对象
     * @author: sunkist
     * @date:2014-11-19
     */
    public static Drawable geRoundDrawableFromUrl(Context context, String url, int pixels) throws Exception {
        byte[] bytes = getBytesFromUrl(url);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) byteToDrawable(bytes);
        return toRoundCorner(context, bitmapDrawable, pixels);
    }

    public static BitmapDrawable toRoundCorner(Context context, BitmapDrawable bitmapDrawable, int pixels) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmapDrawable = new BitmapDrawable(context.getResources(), toRoundCorner(bitmap, pixels));
        return bitmapDrawable;
    }

    /**
     * @param url 字符串路径
     * @return 二进制数据
     * @throws Exception
     * @Description:
     * @see:
     * @since:
     * @description 从路径中得到二进制数据
     * @author: sunkist
     * @date:2014-11-19
     */
    public static byte[] getBytesFromUrl(String url) throws Exception {
        return readInputStream(getRequest(url));
    }

    /**
     * @param inStream 输入流
     * @return
     * @throws Exception
     * @Description:
     * @see:
     * @since: 从流中读取二进制数据
     * @author: sunkist
     * @date:2014-11-19
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * @param path 路径 <a href= "\"http://www.eoeandroid.com/home.php?mod=space&uid=7300\""
     *             target="\"_blank\"">@return</a> 输入流
     * @return
     * @throws Exception
     * @Description:
     * @see:
     * @since:
     * @description 通过路径获取输入流
     * @author: sunkist
     * @date:2014-11-19
     */
    public static InputStream getRequest(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        if (conn.getResponseCode() == 200) {
            return conn.getInputStream();
        }
        return null;
    }

    /**
     * @param byteArray byteArray 二进制数据 字符串路径
     * @return Drawable对象
     * @throws Exception
     * @Description:
     * @see:
     * @since:
     * @description 从二进制数据中得到Drawable对象
     * @author: sunkist
     * @date:2014-11-19
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
        return Drawable.createFromStream(ins, null);
    }

    public static Drawable convertBitmap2Drawable(Context context, Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
        // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        return bd;
    }

    /**
     * @param path
     * @Description: 压缩并保存图片
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-28
     */
    public static Bitmap compressionBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         * options.outHeight为原始图片的高
         */
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
    }

    /**
     * @param resId
     * @param context
     * @return
     * @Description: 以最省内存的方式读取本地资源的图片
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-10
     */
    public static Bitmap readBitMap(int resId, Context context) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * @Title: setViewBack
     * @Description: 兼容处理view 的背景
     */
    public static void drawBackground(Context context, View v, Bitmap backimg) {
        if (context == null || v == null || backimg == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            setBackgroundV16Plus(context, v, backimg);
        } else {
            setBackgroundV16Minus(context, v, backimg);
        }
    }

    @TargetApi(16)
    public static void setBackgroundV16Plus(Context context, View v, Bitmap backimg) {
        v.setBackground(new BitmapDrawable(context.getResources(), backimg));
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundV16Minus(Context context, View v, Bitmap backimg) {
        v.setBackgroundDrawable(new BitmapDrawable(context.getResources(), backimg));
    }

    /**
     * 根据参考视图比例裁切图片
     *
     * @param layoutWidth
     * @param layoutHeight
     * @param origBitmap   原图不会被 recycle
     * @return
     */
    public static Bitmap cropImageByLayoutRatio(int layoutWidth, int layoutHeight, Bitmap origBitmap) {
        float ratioLayout = layoutHeight / (float) layoutWidth;
        float ratioBitmap = origBitmap.getHeight() / (float) origBitmap.getWidth();
        Bitmap bitmap;
        if (ratioLayout > ratioBitmap) {
            // 图片高度不够，裁切左右边
            int w = (int) Math.ceil(origBitmap.getHeight() / ratioLayout);
            int gap = (origBitmap.getWidth() - w) / 2;
            XL.d(TAG, "裁切: " + gap + " " + 0 + " " + w + " " + origBitmap.getHeight());
            bitmap = Bitmap.createBitmap(origBitmap, gap, 0, w, origBitmap.getHeight());
        } else if (ratioLayout < ratioBitmap) {
            // 图片宽度不够，裁切上下边
            int h = (int) Math.ceil(origBitmap.getWidth() * ratioLayout);
            int gap = (origBitmap.getHeight() - h) / 2;
            XL.d(TAG, "裁切: " + 0 + " " + gap + " " + origBitmap.getWidth() + " " + h);
            bitmap = Bitmap.createBitmap(origBitmap, 0, gap, origBitmap.getWidth(), h);
        } else {
            XL.d(TAG, "不裁切");
            bitmap = origBitmap;
        }
        return bitmap;
    }

    public static class Dimension {

        public int width;

        public int height;

        public Dimension(int width, int height) {
            super();
            this.width = width;
            this.height = height;
        }

    }

    /**
     * 获取图片的尺寸
     *
     * @param path
     * @return
     */
    public static Dimension getImageSize(String path) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        BitmapFactory.decodeFile(path, newOpts);
        return new Dimension(newOpts.outWidth, newOpts.outHeight);
    }

    public static class ExifOrientation {

        public boolean flip;

        public int angle;

        public ExifOrientation(boolean flip, int angle) {
            super();
            this.flip = flip;
            this.angle = angle;
        }
    }

    private static final int[] ROTATE = new int[]{0, 0, 0, 180, 180, 90, 90, -90, -90};

    private static final boolean[] FLIP = new boolean[]{false, false, true, false, true, true, false, true, false};

    /**
     * 获取图片的旋转信息
     *
     * @param path
     * @return 找不到就返回null
     */
    public static ExifOrientation getOrientation(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

            if (StringUtil.isNotEmpty(exifOrientation)) {

                try {
                    Integer value = Integer.parseInt(exifOrientation);
                    if (value >= 0 && value <= 8) {
                        return new ExifOrientation(FLIP[value], ROTATE[value]);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "解析数字失败: " + exifOrientation);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "无法读取文件: " + path, e);
        }
        return null;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param orientation
     * @return 如果返回不同的图，传入的图被回收
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, ExifOrientation orientation) {
        if (!orientation.flip && orientation.angle == 0) {
            // 不旋转
            return bitmap;
        }
        Matrix matrix = new Matrix();
        if (orientation.flip) {
            XL.d(TAG, "翻转图片");
            matrix.setScale(-1, 1);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (orientation.angle != 0) {
            // rotate the Bitmap
            XL.d(TAG, "旋转图片: " + orientation.angle);
            matrix.postRotate(orientation.angle);
        }

        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (createBitmap != bitmap) {
            bitmap.recycle();
        }
        return createBitmap;
    }

    /**
     * 从exif正确旋转的坐标系里的点，转回未旋转的坐标系
     *
     * @param orientation
     * @return
     */
    public static Point reverseTranslateAndFlip(Point p, Dimension rect, ExifOrientation orientation) {
        Point origPoint = p;
        XL.d(TAG, "坐标转换: w=" + rect.width + ", h=" + rect.height);
        int angle = orientation.angle;
        if (Math.abs(angle) == 90) {
            rect = new Dimension(rect.height, rect.width);
            XL.d(TAG, "坐标转换(翻转): w=" + rect.width + ", h=" + rect.height);
        }
        if (angle != 0) {
            int revertAngle = angle == 90 ? -90 : angle == 180 ? 180 : 90;
            p = translate(p, rect, revertAngle);
            XL.d(TAG, "坐标转换(角度): angle=" + angle + "->" + revertAngle + ", " + origPoint.x + "x" + origPoint.y + " -> " + p.x + "x" + p.y);
        }
        if (orientation.flip) {
            p = flip(p, rect);
            XL.d(TAG, "坐标转换(翻转): " + origPoint.x + "x" + origPoint.y + " -> " + p.x + "x" + p.y);
        }
        XL.d(TAG, "坐标转换: " + origPoint.x + "x" + origPoint.y + " -> " + p.x + "x" + p.y);
        return p;
    }

    /**
     * 按照Exif旋转后的坐标
     *
     * @param p
     * @param rect
     * @param angle
     * @return
     */
    public static Point translate(Point p, Dimension rect, int angle) {

        if (angle == 90) {
            return new Point(rect.height - p.y, p.x);
        } else if (angle == 180) {
            return new Point(rect.width - p.x, rect.height - p.y);
        } else if (angle == -90) {
            return new Point(p.y, rect.width - p.x);
        } else if (angle == 0) {
            return p;
        }
        return null;
    }

    /**
     * 按照exif翻转后的坐标
     *
     * @param p
     * @param rect
     * @return
     */
    public static Point flip(Point p, Dimension rect) {
        return new Point(rect.width - p.x, p.y);
    }

    public Bitmap createThumbImage(String path, int w, int h, SizeGetCallback cb) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int srcWidth = opts.outWidth; // 获取图片的原始宽度
        int srcHeight = opts.outHeight; // 获取图片原始高度
        XL.d(TAG, "获取缩略图，原图尺寸: " + srcWidth + "x" + srcHeight);

        float ratOrig = srcWidth / (float) srcHeight;
        float ratNew = w / (float) h;
        int wh = Math.min(w, h);
        int destW;
        int destH;
        if (srcWidth < wh || srcHeight < wh) {
            destW = srcWidth;
            destH = srcHeight;
        } else if (ratOrig < ratNew) {
            // 原图高多一些
            destH = h;
            destW = (int) Math.ceil(destH * srcWidth / (float) srcHeight);
        } else {
            destW = w;
            destH = (int) Math.ceil(destW * srcHeight / (float) srcWidth);
        }

        int r = srcWidth / destW;
        // 找到缩小最合适的倍数，缩放只能用2的倍数(1/2,1/4,1/8...)，找到个比预期稍大的
        // 直接使用按照sample采样获取的图，不再缩放了，减少缩放开销
        int sampleSize = 1;
        while (sampleSize <= r) {
            sampleSize *= 2;
        }

        sampleSize = Math.max(1, sampleSize / 2);

        XL.d(TAG, "inSampleSize=" + sampleSize + " (" + r + ")");
        int sampleW = (int) Math.ceil(srcWidth / (float) sampleSize);
        int sampleH = (int) Math.ceil(srcHeight / (float) sampleSize);
        while (sampleW > 2600 || sampleH > 2600) {
            // 最后缩放出来的的图一定要小于3000
            // 否则可能oom
            sampleSize *= 2;
            sampleW = (int) Math.ceil(srcWidth / (float) sampleSize);
            sampleH = (int) Math.ceil(srcHeight / (float) sampleSize);
        }

        XL.d(TAG, "缩放: " + srcWidth + "x" + srcHeight + "(orig) -> " //
                + w + "x" + h + "(expect) -> " //
                + destW + "x" + destH + "(best) -> " //
                + "" + sampleW + "x" + sampleH + "(real)");

        opts = new BitmapFactory.Options();
        // 缩放的比例
        opts.inSampleSize = sampleSize;
        Bitmap decodeFile = BitmapFactory.decodeFile(path, opts);

        ExifOrientation orientation = ImageUtil.getOrientation(path);
        cb.getSize(srcWidth, srcHeight, sampleW, sampleH, 1 / (float) sampleSize, orientation);
        if (orientation != null && (orientation.flip || orientation.angle != 0)) {
            // 需要旋转
            XL.d(TAG, "旋转图片: flip=" + orientation.flip + ", angle=" + orientation.angle + ", path=" + path);
            Bitmap rotateFile = ImageUtil.rotateBitmap(decodeFile, orientation);
            if (rotateFile != decodeFile) {
                decodeFile.recycle();
            }
            decodeFile = rotateFile;

        }
        return decodeFile;
    }

    public interface SizeGetCallback {

        /**
         * 实际缩放运算前被调用，用来获得缩放后的尺寸
         *
         * @param srcW       原图
         * @param srcH       原图
         * @param thumbW     缩图
         * @param thumbH     原图
         * @param scaleRatio 缩放比例
         */
        public void getSize(int srcW, int srcH, int thumbW, int thumbH, float scaleRatio, ExifOrientation orientation);
    }

    static int FOREGROUND_COLOR = 0xff000000;
    static int BACKGROUND_COLOR = 0xffffffff;

    /**
     * @param captureActivity
     * @Description:在用户登录的时候生成一张二维码
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-22
     */
    public static Bitmap createTwoDimentsionalCode(CaptureActivity captureActivity, String str, Bitmap icon, int ewmSize) {
        // if (icon != null) {
        // icon = ImageUtil.tDCIcon(icon, IMAGE_HALFWIDTH);
        // }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // hints.put(EncodeHintType., 1);
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, ewmSize, ewmSize, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (matrix != null) {
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // if (icon != null && x > halfW - IMAGE_HALFWIDTH && x <
                    // halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH &&
                    // y < halfH + IMAGE_HALFWIDTH) {
                    // pixels[y * width + x] = icon.getPixel(x - halfW +
                    // IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                    // } else {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = FOREGROUND_COLOR;
                    } else {
                        pixels[y * width + x] = BACKGROUND_COLOR;
                    }
                    // }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap getSuperpositionBitmap(Bitmap b1, Bitmap b2,//
                                                int zoomW, int zoomH,//
                                                float bxW, float bxH, //
                                                int gapW, int gapH) {

        Bitmap bitmap = b1.copy(Bitmap.Config.RGB_565, true);
        b2 = zoomBitmap(b2, zoomW, zoomH);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        float b1w = bitmap.getWidth();
        float b1h = bitmap.getHeight();
        float b2w = b2.getWidth();
        float b2h = b2.getHeight();
        float bx = ((b1w - b2w) / bxW) - gapW;
        float by = ((b1h - b2h) / bxH) - gapH;
        canvas.drawBitmap(b2, bx, by, paint);
        // 叠加新图b2 并且居中
        canvas.save();
        canvas.restore();
        return bitmap;

    }

    public static Bitmap getTwoDimentsionalCode(Bitmap b1, Bitmap b2, int zoomW, int zoomH, float bxW, float bxH) {
        Bitmap bitmap = b1.copy(Bitmap.Config.ARGB_8888, true);
        b2 = zoomBitmap(b2, zoomW, zoomH);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        float b1w = bitmap.getWidth();
        float b1h = bitmap.getHeight();
        float b2w = b2.getWidth();
        float b2h = b2.getHeight();
        float bx = (b1w - b2w) / bxW;
        float by = (b1h - b2h) / bxH;
        canvas.drawBitmap(b2, bx, by, paint);
        // 叠加新图b2 并且居中
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public static Bitmap getTwoDimentsionalCode(Bitmap b1, Bitmap b2) {
        Bitmap bitmap = b1.copy(Bitmap.Config.ARGB_8888, true);
        b2 = zoomBitmap(b2, 85, 85);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        int b1w = bitmap.getWidth();
        int b1h = bitmap.getHeight();
        int b2w = b2.getWidth();
        int b2h = b2.getHeight();
        int bx = (b1w - b2w) / 2;
        int by = (b1h - b2h) / 2;
        canvas.drawBitmap(b2, bx, by, paint);
        // 叠加新图b2 并且居中
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public static Bitmap drawRound(Bitmap thumb, int roundWidth) {
        Bitmap round = Bitmap.createBitmap(thumb.getWidth() + roundWidth, thumb.getHeight() + roundWidth, thumb.getConfig());
        Canvas cv = new Canvas(round);
        cv.drawColor(Color.WHITE);
        cv.drawBitmap(thumb, roundWidth / 2 + 1, roundWidth / 2 + 1, null);
        return round;
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 320) {
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();
            // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;
            // 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static void getFacebookShareImage(final User user, final String imgUrl, final CallbackImpl callbackImpl, final Activity activity) {
        XL.d("imgUrl", "imgUrl=" + imgUrl);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) activity.getResources().//
                getDrawable(R.drawable.share_sina_code);
        final Bitmap bitmap = bitmapDrawable.getBitmap();
        AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

            @Override
            public void run() {
                Bitmap thumb = ImageUtil.handPic(imgUrl);
                Bitmap roundBitmap = ImageUtil.getRoundBitmap(thumb);
                Bitmap layerThumb = ImageUtil.getSuperpositionBitmap(bitmap, roundBitmap, 600, 600, 2, 2, 0, 32);
                String filePath = ImageUtil.saveToLocal(layerThumb, FileTools.getAlohaImgPath() + "/" + user.getId() + ".jpg");
                XL.d("imgUrl", "filePath=" + filePath);
                callbackImpl.success(filePath);
            }
        });
    }

    /**
     * 根据屏幕尺寸剪裁指定路径的图片
     *
     * @param context
     * @param path    图片的本地路径
     * @return Bitmap
     */
    public static Bitmap creatBitmapBySrceenSize(Context context, String path) throws Exception {

        Bitmap origBitmap = BitmapFactory.decodeFile(path);
        int layoutH = UiUtils.getScreenHeight(context);
        int layoutW = UiUtils.getScreenWidth(context);
        float ratioLayout = layoutH / (float) layoutW;
        float ratioBitmap = origBitmap.getHeight() / (float) origBitmap.getWidth();
        Bitmap bitmap;
        if (ratioLayout > ratioBitmap) {
            // 图片高度不够，裁切左右边
            int w = (int) Math.ceil(origBitmap.getHeight() / ratioLayout);
            int gap = (origBitmap.getWidth() - w) / 2;
            // 裁切: " + gap + " " + 0 + " " + w + " " +
            // origBitmap.getHeight());
            bitmap = Bitmap.createBitmap(origBitmap, gap, 0, w, origBitmap.getHeight());
        } else if (ratioLayout < ratioBitmap) {
            // 图片宽度不够，裁切上下边
            int h = (int) Math.ceil(origBitmap.getWidth() * ratioLayout);
            int gap = (origBitmap.getHeight() - h) / 2;
            // 裁切: " + 0 + " " + gap + "
            // " + origBitmap.getWidth() + " " + h);
            bitmap = Bitmap.createBitmap(origBitmap, 0, gap, origBitmap.getWidth(), h);
        } else {
            // 不裁切");
            bitmap = origBitmap;
        }
        return bitmap;
    }
}
