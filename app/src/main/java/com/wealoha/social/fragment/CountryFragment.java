package com.wealoha.social.fragment;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.InjectView;

import com.wealoha.social.R;
import com.wealoha.social.beans.RegionNode;
import com.wealoha.social.utils.RegionNodeUtil;

public class CountryFragment extends BaseFragment {

    private View thisView;

    @InjectView(R.id.location_country_list)
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        thisView = inflater.inflate(R.layout.act_location_list, container, false);
        RegionNodeUtil rnu = new RegionNodeUtil();
        Log.i("location", rnu.getByCode("DE").getName() + "-----" + rnu.getByCode("DE").getName());

        ArrayList<String> locations = new ArrayList<String>();
        Set<Entry<String, RegionNode>> sets = rnu.getRegionNodeMap().entrySet();
        for (Entry<String, RegionNode> entry : sets) {
            String key = (String) entry.getKey();
            RegionNode value = (RegionNode) entry.getValue();
            Log.i("location", "key:" + key);
            Log.i("location", "value:" + value.getName());
            locations.add(value.getName());
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.act_loaction_item, R.id.location_item_name, locations);
        listView.setAdapter(locationAdapter);
        return thisView;
    }

}
