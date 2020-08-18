package com.wealoha.social.widget;

/**
 * 如果一个List的数据支持多种视图，需要增加一个枚举实现这个接口
 * 
 * @author javamonk
 * @createTime 2015年3月4日 上午11:08:32
 */
public interface MultiListViewType {

	/**
	 * 
	 * @return 范围：从0开始，到视图数-1，必须连续
	 */
	public int getViewType();
}
