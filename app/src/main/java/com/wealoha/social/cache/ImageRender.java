package com.wealoha.social.cache;

import java.io.File;

import javax.inject.Inject;

import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.wealoha.social.inject.Injector;

/**
 * 代理Picasso的图片渲染
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-18 下午3:44:23
 */
public class ImageRender {

	@Inject
	Picasso picasso;

	public ImageRender() {
		Injector.inject(this);
	}

	public ImageRenderRender load(File file) {
		return new ImageRenderRender(picasso.load(file));
	}

	public ImageRenderRender load(int resourceId) {
		return new ImageRenderRender(picasso.load(resourceId));
	}

	public ImageRenderRender load(String path) {
		return new ImageRenderRender(picasso.load(path));
	}

	public ImageRenderRender load(Uri uri) {
		return new ImageRenderRender(picasso.load(uri));
	}

}
