package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * 地区
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-28 下午2:43:28
 */
public class RegionNode implements Serializable {

	private static final long serialVersionUID = -5175776288479428533L;
	public static final String TAG = RegionNode.class.getName();
	public String name;
	/** 缩写，如果有优先显示缩写 */
	public String abbr;

	public Map<String, RegionNode> regions;

	public RegionNode(String name, String abbr, Map<String, RegionNode> regions) {
		super();
		this.name = name;
		this.abbr = abbr;
		this.regions = regions;
	}

}
