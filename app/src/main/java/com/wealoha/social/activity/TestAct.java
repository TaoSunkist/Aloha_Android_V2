package com.wealoha.social.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.wealoha.social.R;
import com.wealoha.social.widget.ScrollToLoadMoreListView;

public class TestAct extends Activity {

	private ScrollToLoadMoreListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		listView = (ScrollToLoadMoreListView) findViewById(R.id.list_view);
		ArrayList<String> strs = new ArrayList<>();
		strs.add("dddddddddddddd");
		strs.add("dddddddddddddd");
		strs.add("dddddddddddddd");
		strs.add("dddddddddddddd");
		// View view = LayoutInflater.from(this).inflate(R.layout.list_loader_footer, null);
		// listView.addFooterView(view);
		ArrayAdapter<String> strAdapter = new ArrayAdapter<>(this, R.layout.test_list_view, strs);
		// listView.setAdapter(strAdapter);
	}
}
