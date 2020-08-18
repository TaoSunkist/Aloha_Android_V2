package com.wealoha.social.view.custom.preferenceitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;

public class ProfileBlockItem extends SimpleBlockItem {

	protected final String username;
	protected final String imageUrl;

	public ProfileBlockItem(BlockItemCallback itemCallback, int titleResource, String hint,//
			String username, String imageUrl, boolean hasArrowImg, boolean hasFirstTag) {
		super(itemCallback, titleResource, hint, hasArrowImg, hasFirstTag);

		this.username = username;
		this.imageUrl = imageUrl;
	}

	@Override
	public void initView(LayoutInflater inflater, ViewGroup blockView, int index) {
		RelativeLayout itemroot = (RelativeLayout) inflater.inflate(R.layout.item_preferense_profileview, blockView, false);
		TextView titleText = (TextView) itemroot.findViewById(R.id.title);
		TextView nameText = (TextView) itemroot.findViewById(R.id.user_name);
		ImageView userPhotoImg = (ImageView) itemroot.findViewById(R.id.userphoto);
		View firstSub = itemroot.findViewById(R.id.first_sub);

		Context context = itemClickCallback.getContext();
		Picasso.with(context).load(imageUrl).placeholder(R.drawable.default_photo).into(userPhotoImg);
		titleText.setText(title);
		nameText.setText(username);
		isViewVisibility(hasFirstTag, firstSub);

		blockView.addView(itemroot);
	}
}
