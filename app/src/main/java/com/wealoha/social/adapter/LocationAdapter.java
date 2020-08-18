package com.wealoha.social.adapter;

import java.util.ArrayList;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;

public class LocationAdapter extends BaseAdapter {

	@Inject
	FontUtil fontUtil;
	private ArrayList<String> values;
	private Context context;

	public LocationAdapter(Context context, ArrayList<String> values, ArrayList<String> keys) {
		Injector.inject(this);
		this.values = values;
		this.context = context;
	}

	@Override
	public int getCount() {

		return values.size();
	}

	@Override
	public Object getItem(int position) {

		return values.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.act_loaction_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String areaName = values.get(position);
		if (areaName.endsWith("*")) {
			viewHolder.img.setVisibility(View.VISIBLE);
			areaName = areaName.substring(0, areaName.length() - 1);
		} else {
			viewHolder.img.setVisibility(View.GONE);
		}
		viewHolder.tv.setText(areaName);
		return convertView;
	}

	class ViewHolder {

		TextView tv;
		ImageView img;

		public ViewHolder(View view) {
			tv = (TextView) view.findViewById(R.id.location_item_name);
			img = (ImageView) view.findViewById(R.id.location_item_sign);
			fontUtil.changeViewFont(tv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		}
	}

}
