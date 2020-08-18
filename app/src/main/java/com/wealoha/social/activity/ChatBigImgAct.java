package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.UiUtils;

public class ChatBigImgAct extends BaseFragAct implements OnClickListener{

	@Inject
	Picasso picasso;
	@Inject
	Context context;
	@InjectView(R.id.chat_big_img_back)
	TextView mBack;

	@InjectView(R.id.chat_big_img)
	ImageView mBigImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		setContentView(R.layout.act_chat_big_img);
		initData();
	}

	public void initData() {
		Bundle bundle = getIntent().getExtras();
		String url = "";
		String imageid = "";
		if (bundle != null) {
			url = bundle.getString("imgurl");
			imageid = bundle.getString("imgid");
		}

		Log.i("BIGIMAGE", "imageid:" + imageid);
		Log.i("BIGIMAGE", "url:" + url);

		if (!TextUtils.isEmpty(url)) {
			picasso.load(url).into(mBigImg);
		} else if (!TextUtils.isEmpty(imageid)) {
			picasso.load(ImageUtil.getImageUrl(imageid, UiUtils.getScreenWidth(context), CropMode.ScaleCenterCrop)).into(mBigImg);
		}

	}

	@OnClick({ R.id.chat_big_img_back })
	@Override
	public void onClick(View v) {
		// super.onClick(v);

		switch (v.getId()) {
		case R.id.chat_big_img_back:
			finish();
			break;

		default:
			break;
		}
	}

}
