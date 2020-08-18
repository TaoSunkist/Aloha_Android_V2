package com.wealoha.social.view.custom.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wealoha.social.R;

public class LoadingPopup {

	private PopupWindow pop;
	private Dialog loading;
	private View mView;

	public LoadingPopup(Context context) {
		LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mLayoutInflater.inflate(R.layout.popup_prompt, null);
		pop = new PopupWindow(mView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
	}

	public void show(View view) {
		if (pop == null || pop.isShowing()) {
			return;
		}

		pop.showAtLocation(view, Gravity.CENTER, 0, 0);
		pop.update();
	}

	public void show() {
		if (pop == null || pop.isShowing()) {
			return;
		}

		pop.showAtLocation(mView, Gravity.CENTER, 0, 0);
		pop.update();
	}

	public void hideDialog() {
		if (loading == null || !loading.isShowing()) {
			return;
		}

		loading.dismiss();
	}

	public void hide() {
		if (pop == null || !pop.isShowing()) {
			return;
		}
		pop.dismiss();
	}

}
