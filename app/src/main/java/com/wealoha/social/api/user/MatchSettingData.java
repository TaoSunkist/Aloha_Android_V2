package com.wealoha.social.api.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.region.Regions;

public class MatchSettingData extends ResultData implements Serializable {

	private static final long serialVersionUID = -6856543043928783808L;
	public static final String TAG = MatchSettingData.class.getName();
	public boolean filterEnable;
	public String filterRegion;
	public Integer filterAgeRangeStart;
	public Integer filterAgeRangeEnd;
	public ArrayList<Regions> regions;
	public List<String> selectedRegion;

}
