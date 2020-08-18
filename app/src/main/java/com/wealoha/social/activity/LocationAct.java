package com.wealoha.social.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.LocationAdapter;
import com.wealoha.social.api.user.MatchSettingData;
import com.wealoha.social.beans.RegionNode;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.RegionNodeUtil;

public class LocationAct extends BaseFragAct implements OnClickListener {

	@Inject
	FontUtil fontUtil;

	@InjectView(R.id.location_country_list)
	ListView listView;
	@InjectView(R.id.location_back)
	ImageView mBack;
	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;

	private static int LOACTIONTAG = 1;

	private RegionNodeUtil rnu;
	private ArrayList<String> values;
	private ArrayList<String> keys;
	private Map<String, RegionNode> regionNodes;
	private String regionName;
	private Context mContext;
	public static final String FROM_SERVER = "FROM_SERVER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_location_list);
		mContext = this;

		mBack.setOnClickListener(this);
		boolean fromServer = getIntent().getBooleanExtra(FROM_SERVER, false);
		if (fromServer) {
			initDataFromServer();
		} else {
			initData();
		}

	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	public void initDataFromServer() {
		rnu = new RegionNodeUtil();
		MatchSettingData msd = ((MatchSettingData) getIntent().getSerializableExtra(MatchSettingData.TAG));
		if (msd == null) {
			regionNodes = ((RegionNode) getIntent().getSerializableExtra(RegionNode.TAG)).regions;
		} else {
			// regionNodes = msd.regions;
		}

		values = new ArrayList<String>();
		keys = new ArrayList<String>(regionNodes.keySet());
		Collections.sort(keys); // 排序
		for (String key : keys) {
			RegionNode value = regionNodes.get(key);
			if (value.regions != null && value.regions.size() > 0) {
				values.add(value.name + "*");
			} else {
				values.add(value.name);
			}
		}

		LocationAdapter locationAdapter = new LocationAdapter(mContext, values, keys);
		listView.setAdapter(locationAdapter);

		// regionCode = getIntent().getStringExtra("regionCode");
		regionName = getIntent().getStringExtra("regionName");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(RegionNode.TAG, regionNodes.get(keys.get(position)));
				bundle.putBoolean(FROM_SERVER, true);
				Intent intent = new Intent();
				// 去掉*
				String areaName = values.get(position);
				if (areaName.endsWith("*")) {
					areaName = areaName.substring(0, areaName.length() - 1);
				}
				if (regionName != null) {
					bundle.putString("regionName", regionName + ", " + areaName);
				} else {
					bundle.putString("regionName", areaName);
				}
				if (regionNodes.get(keys.get(position)).regions != null) {
					intent.putExtras(bundle);
					intent.setData(GlobalConstants.IntentAction.INTENT_URI_LOCATION);
					startActivityForResult(intent, LOACTIONTAG);
				} else {
					bundle.putString("lastRegionName", values.get(position));
					bundle.putString("lastRegionNameKey", keys.get(position));
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	public void initData() {
		rnu = new RegionNodeUtil();
		String region = getIntent().getStringExtra("region");

		if (TextUtils.isEmpty(region)) {
			regionNodes = rnu.getRegionNodeMap();
		} else {
			regionNodes = rnu.getByCode(region).regions;
		}

		values = new ArrayList<String>();
		keys = new ArrayList<String>(regionNodes.keySet());
		Collections.sort(keys); // 排序
		for (String key : keys) {
			RegionNode value = regionNodes.get(key);
			if (value.regions != null && value.regions.size() > 0) {
				values.add(value.name + "*");
			} else {
				values.add(value.name);
			}
		}

		LocationAdapter locationAdapter = new LocationAdapter(mContext, values, keys);
		listView.setAdapter(locationAdapter);

		// regionCode = getIntent().getStringExtra("regionCode");
		regionName = getIntent().getStringExtra("regionName");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString("region", keys.get(position));
				Intent intent = new Intent();
				// 去掉*
				String areaName = values.get(position);
				if (areaName.endsWith("*")) {
					areaName = areaName.substring(0, areaName.length() - 1);
				}
				if (regionName != null) {
					bundle.putString("regionName", regionName + ", " + areaName);
				} else {
					bundle.putString("regionName", areaName);
				}
				if (regionNodes.get(keys.get(position)).regions != null) {
					intent.putExtras(bundle);
					intent.setData(GlobalConstants.IntentAction.INTENT_URI_LOCATION);
					startActivityForResult(intent, LOACTIONTAG);
				} else {
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		setResult(RESULT_OK, result);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_back:
			finish();
			break;
		default:
			break;
		}
	}
}
