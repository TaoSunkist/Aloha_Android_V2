package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.ImageUtil;

public class LauncherImgAct extends BaseFragAct {

	@Inject
	ContextUtil contextUtil;
	@Inject
	Picasso picasso;
	@Inject
	Context context;
	private ImageView launchImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Injector.inject(this);

		setContentView(R.layout.act_launcher);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		launchImg = (ImageView) findViewById(R.id.launcher_img);
		loadImg();

		new CountDownTimer(3000, 3000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				finish();
			}
		}.start();
	}

	/**
	 * 从本地加载开机画面，如果不存在则直接退出
	 * 
	 * @return void
	 */
	private void loadImg() {
		String path = contextUtil.getStartupImagePath();
		if (TextUtils.isEmpty(path) || !FileTools.isExists(path)) {
			finish();
			return;
		}
		try {
			launchImg.setImageBitmap(ImageUtil.creatBitmapBySrceenSize(mContext, path));
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}
