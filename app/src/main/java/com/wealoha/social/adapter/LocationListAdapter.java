package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wealoha.social.R;
import com.wealoha.social.beans.Regions;
import com.wealoha.social.utils.FontUtil;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月9日
 */
public class LocationListAdapter extends BaseAdapter {

	private List<Regions> mRegions;
	private Activity mParent;
	private LayoutInflater mLayoutInflater;

	// private LinkedMap<Regions, Regions> mRegionMap;

	public LocationListAdapter(Activity act, ArrayList<Regions> regions) {
		mRegions = new ArrayList<>();
		mParent = act;
		for (int i = 0; i < regions.size(); i++) {// 转换成Map存储
			regions.get(i).name += " ";
			mRegions.add(regions.get(i));
			for (int j = 0; j < regions.get(i).regions.size(); j++) {
				mRegions.add(regions.get(i).regions.get(j));
			}
		}

		mLayoutInflater = (LayoutInflater) mParent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mRegions.size();
	}

	@Override
	public Regions getItem(int position) {
		return mRegions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 初始化头部
		View viewItem = null;
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewItem = mLayoutInflater.inflate(R.layout.item_location_list, null);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) viewItem.findViewById(R.id.location_item_name);
			viewHolder.viewLine = viewItem.findViewById(R.id.location_item_line);
			FontUtil.setSemiBoldTypeFace(mParent, viewHolder.text);
			viewItem.setTag(viewHolder);
		} else {
			viewItem = convertView;
			viewHolder = (ViewHolder) viewItem.getTag();
		}
		if (mRegions.get(position).name.endsWith(" ")) {
			viewItem.setBackgroundResource(R.color.act_bg);
			viewHolder.viewLine.setVisibility(View.GONE);
			viewHolder.text.setTextColor(mParent.getResources().getColor(R.color.gray_text));
			viewItem.setClickable(false);
		} else {
			viewHolder.viewLine.setVisibility(View.VISIBLE);
			// 处理下划线的代码
			if (position < mRegions.size() - 1 && mRegions.get(position + 1).name.endsWith(" ")) {
				viewHolder.viewLine.setVisibility(View.GONE);
			}
			viewItem.setBackgroundColor(Color.WHITE);
			viewHolder.text.setTextColor(mParent.getResources().getColor(R.color.black_text));
			viewHolder.regions = mRegions.get(position);
			viewItem.setClickable(true);
			viewItem.setBackgroundColor(Color.WHITE);
			viewItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("lastRegionNameKey", viewHolder.regions.code);
					intent.putExtra("lastRegionName", viewHolder.regions.name);
					mParent.setResult(Activity.RESULT_OK, intent);
					mParent.overridePendingTransition(R.anim.right_out, R.anim.stop);
					mParent.finish();
				}
			});
		}
		viewHolder.text.setText(mRegions.get(position).name);
		return viewItem;
	}

	public static class ViewHolder {

		public TextView text;
		public View viewLine;
		public Regions regions;
	}
}
