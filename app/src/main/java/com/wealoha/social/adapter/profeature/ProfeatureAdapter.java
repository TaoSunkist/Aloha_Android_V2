package com.wealoha.social.adapter.profeature;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wealoha.social.api.profeature.bean.Profeature;

public class ProfeatureAdapter extends BaseAdapter {

	private List<Profeature> mProfeatList;
	private Activity mAct;

	public ProfeatureAdapter(List<Profeature> profeatList, Activity act) {
		if (profeatList == null) {
			mProfeatList = new ArrayList<Profeature>();
		} else {
			mProfeatList = profeatList;
		}
		mAct = act;
	}

	@Override
	public int getCount() {
		return mProfeatList.size();
	}

	@Override
	public Profeature getItem(int position) {
		return mProfeatList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProfeatureItemHolder pfHolder;
		if (convertView == null) {
			pfHolder = new ProfeatureItemHolder(mAct.getLayoutInflater(), parent);
		} else {
			pfHolder = (ProfeatureItemHolder) convertView.getTag();
		}
		convertView = pfHolder.setProfeatureData(getItem(position));
		convertView.setTag(pfHolder);
		return convertView;
	}
}
