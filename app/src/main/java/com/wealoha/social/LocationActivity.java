package com.wealoha.social;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.adapter.LocationListAdapter;
import com.wealoha.social.beans.MatchSettingData;

public class LocationActivity extends BaseFragAct {
	@InjectView(R.id.location_country_listview)
	ListView mListView;
	LocationListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		MatchSettingData mMatchSettingData = (MatchSettingData) getIntent().getSerializableExtra(MatchSettingData.TAG);
		if (mMatchSettingData != null && mMatchSettingData.regions != null && mMatchSettingData.regions.size() > 0) {
			mAdapter = new LocationListAdapter(this, mMatchSettingData.regions);
			mListView.setAdapter(mAdapter);	
		} else {
			overridePendingTransition(R.anim.stop, R.anim.right_out);
			finish();
			return;
		}

	}

	@OnClick({ R.id.location_back })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_back:
			overridePendingTransition(R.anim.stop, R.anim.right_out);
			finish();
			break;
		}
	}
}
