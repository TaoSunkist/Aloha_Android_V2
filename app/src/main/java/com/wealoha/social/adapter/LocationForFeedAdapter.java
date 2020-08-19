package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.beans.Location;

public class LocationForFeedAdapter extends BaseAdapter {

	private List<Location> mLocations;
	private Context mContext;

	public LocationForFeedAdapter(Context context, List<Location> locations) {
		if (locations != null) {
			mLocations = locations;
		} else {
			mLocations = new ArrayList<Location>();
		}
		mContext = context;
	}

	public void notifyDataSetChangedByResult(List<Location> locations) {
		if (locations != null) {
			mLocations.addAll(locations);
			super.notifyDataSetChanged();
		}
	}

	public void notifyDataSetChangedBySearch(List<Location> locations) {
		if (locations != null) {
			mLocations = locations;
			super.notifyDataSetChanged();
		}
	}

	public void clear() {
		if (mLocations != null) {
			mLocations.clear();
			super.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mLocations.size();
	}

	@Override
	public Object getItem(int position) {
		return mLocations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_location_for_feed, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Location location = (Location) getItem(position);

		holder.address.setText(location.address);
		holder.addressName.setText(location.name);
		return convertView;
	}

	static class ViewHolder {

		TextView addressName;
		TextView address;

		public ViewHolder(View view) {
			addressName = (TextView) view.findViewById(R.id.location_address_name);
			address = (TextView) view.findViewById(R.id.location_address);
		}
	}

}
