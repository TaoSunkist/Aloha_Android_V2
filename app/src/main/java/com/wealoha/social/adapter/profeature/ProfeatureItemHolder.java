package com.wealoha.social.adapter.profeature;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.beans.Profeature;

public class ProfeatureItemHolder {

	private View rootView;
	private ImageView headerImg;
	private TextView titleTv;
	private TextView explainTv;
	private TextView stateTv;

	public ProfeatureItemHolder(LayoutInflater inflater, ViewGroup parent) {
		rootView = inflater.inflate(R.layout.item_profeature_content, parent, false);
		headerImg = (ImageView) rootView.findViewById(R.id.header_img);
		titleTv = (TextView) rootView.findViewById(R.id.title_tv);
		explainTv = (TextView) rootView.findViewById(R.id.explain_tv);
		stateTv = (TextView) rootView.findViewById(R.id.state_tv);
	}

	public View initView(Profeature pfData) {
		headerImg.setImageResource(pfData.getHeaderResId());
		titleTv.setText(pfData.getTitle());
		explainTv.setText(pfData.getExplain());
		stateTv.setText(pfData.getState());
		return rootView;
	}

	public View setProfeatureData(Profeature pfData) {
		return initView(pfData);
	}

}
