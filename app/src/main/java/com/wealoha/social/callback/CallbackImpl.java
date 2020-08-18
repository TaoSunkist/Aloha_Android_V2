package com.wealoha.social.callback;

import android.graphics.Bitmap;
import android.widget.PopupWindow;

import com.sina.weibo.sdk.api.ImageObject;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年5月29日
 */
public class CallbackImpl implements ShareCallback {

	@Override
	public void success() {

	}

	public void success(String string) {

	}

	@Override
	public void failure() {

	}

	public void failure(boolean isSuccess) {

	}

	public void success(ImageObject imageObject, Bitmap bitmap) {

	}

	public void error() {

	}

	public void closePopupWindow(PopupWindow popupWindow) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

}
