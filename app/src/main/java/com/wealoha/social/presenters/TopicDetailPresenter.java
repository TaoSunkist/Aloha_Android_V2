package com.wealoha.social.presenters;

import javax.inject.Inject;

import com.wealoha.social.api.common.service.BaseService.ServiceObjResultCallback;
import com.wealoha.social.api.TopicPostService;
import com.wealoha.social.beans.TopicPosts;
import com.wealoha.social.api.topic.bean.HashTag;
import com.wealoha.social.ui.topic.ITopicDetailView;

public class TopicDetailPresenter extends AbsPresenter {

	@Inject
	TopicPostService postService;
	ITopicDetailView mITopicDetailView;
	private HashTag mHashTag;

	public TopicDetailPresenter(ITopicDetailView iTopicDetailView) {
		super();
		mITopicDetailView = iTopicDetailView;
	}

	public void getTopicData(String tagname) {
		postService.getResult(tagname, new ServiceObjResultCallback<TopicPosts>() {

			@Override
			public void success(TopicPosts t) {
				if (t != null && t.getHashTag() != null) {
					mHashTag = t.getHashTag();
				}
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.notifyDataChange(t);
			}

			@Override
			public void failer() {
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.showReloadFooterView();
			}

			@Override
			public void nomore() {
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.showNomoreFooterView();
			}

			@Override
			public void beforeSuccess() {
				mITopicDetailView.hideRefreshView();

			}
		});
	}

	public void getTopicData(HashTag hashTag) {
		postService.getResult(hashTag.getItemId(), new ServiceObjResultCallback<TopicPosts>() {

			@Override
			public void success(TopicPosts t) {
				if (t != null && t.getHashTag() != null) {
					mHashTag = t.getHashTag();
				}
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.notifyDataChange(t);
			}

			@Override
			public void failer() {
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.showReloadFooterView();
			}

			@Override
			public void nomore() {
				mITopicDetailView.hideRefreshView();
				mITopicDetailView.showNomoreFooterView();
			}

			@Override
			public void beforeSuccess() {
				mITopicDetailView.hideRefreshView();

			}
		});
	}

	/**
	 * 初始化数据。主要用来控制不同情况获取数据时，视图的变化。实际调用请求的方法是{@link #getTopicData(String)}
	 * 
	 * @return void
	 */
	public void initTopicData(String tagname) {
		getTopicData(tagname);
	}

	/**
	 * 刷新数据。主要用来控制不同情况获取数据时，视图的变化。实际调用请求的方法是{@link #getTopicData(String)}
	 * 
	 * @return void
	 */
	public void refreshTopicData(String tagname) {
		postService.refreshAllData();
		getTopicData(tagname);
	}

	/**
	 * 拉取更多数据。主要用来控制不同情况获取数据时，视图的变化。实际调用请求的方法是{@link #getTopicData(String)}
	 * 
	 * @return void
	 */
	public void loadMoreTopicData(String tagname) {
		mITopicDetailView.showLoadingFooterView();
		getTopicData(tagname);
	}

	public TopicPosts getTopicData() {
		return null;
	}

	public void byHashtagIdGetData() {
	}

}
