package com.wealoha.social.ui.topic;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wealoha.social.beans.TopicPosts;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月22日
 */
public interface ITopicDetailView extends SwipeRefreshLayout.OnRefreshListener, ListItemCallback {

	/**
	 * @Description:关闭话题页
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月22日
	 */
	public void closeTopicDetailView();

	public void notifyDataChange(TopicPosts t);

	public void hideRefreshView();

	public void showRefreshView();

	public void showNomoreFooterView();

	public void showReloadFooterView();

	public void showLoadingFooterView();

	public void initView(TopicPosts topicPosts);


}
