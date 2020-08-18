package com.wealoha.social.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-18 下午3:46:25
 */
public class ImageRenderRender {

	private final RequestCreator requestCreator;

	public ImageRenderRender(RequestCreator requestCreator) {
		this.requestCreator = requestCreator;
	}

	public ImageRenderRender error(Drawable errorDrawable) {
		requestCreator.error(errorDrawable);
		return this;
	}

	public void into(final ImageView target) {
		requestCreator.into(new Target() {

			@Override
			public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
				target.setImageBitmap(bitmap);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {

			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {

			}


		});
	}

	public void into(Target arg0) {
		requestCreator.into(arg0);
	}

	public ImageRenderRender placeholder(Drawable placeholderDrawable) {
		requestCreator.placeholder(placeholderDrawable);
		return this;
	}

	public ImageRenderRender resize(int targetWidth, int targetHeight) {
		requestCreator.resize(targetWidth, targetHeight);
		return this;
	}

	public ImageRenderRender resizeDimen(int targetWidthResId, int targetHeightResId) {
		requestCreator.resizeDimen(targetWidthResId, targetHeightResId);
		return this;
	}

}
