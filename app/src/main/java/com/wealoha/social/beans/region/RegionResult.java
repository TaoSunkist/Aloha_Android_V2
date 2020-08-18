package com.wealoha.social.beans.region;

import java.io.Serializable;
import java.util.ArrayList;

import com.wealoha.social.beans.ResultData;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月9日
 */
public class RegionResult extends ResultData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1853858837485072423L;
	public boolean filterEnable;
	public int filterAgeRangeStart;
	public int filterAgeRangeEnd;
	public String filterRegion;
	public ArrayList<Regions> regions;
	public ArrayList<String> selectedRegion;
}
