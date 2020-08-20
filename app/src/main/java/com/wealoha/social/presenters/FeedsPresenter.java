package com.wealoha.social.presenters;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;

import com.wealoha.social.api.common.service.BaseService.ServiceListResultCallback;
import com.wealoha.social.api.common.service.BaseService.ServiceResultCallback;
import com.wealoha.social.api.PostService;
import com.wealoha.social.api.TopicPostService;
import com.wealoha.social.beans.Post;
import com.wealoha.social.api.topic.bean.HashTag;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.model.feeds.AdvertisementModel;
import com.wealoha.social.model.feeds.IAdvertisementModel;
import com.wealoha.social.ui.feeds.IFeedsView;
import com.wealoha.social.ui.feeds.IFeedsViewV2;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月21日
 */
public class FeedsPresenter extends AbsPresenter {

	private IFeedsView mIFeedsView;
	private IAdvertisementModel mIAdvertisementModel;
	private Context mCtx;
	@Inject
	TopicPostService mTopicPostService;

	PostService mPostService;

	private IFeedsViewV2 feedsViewV2;

	public FeedsPresenter(Context ctx, IFeedsView iFeedsView) {
		Injector.inject(this);
		mIFeedsView = iFeedsView;
		mIAdvertisementModel = new AdvertisementModel();
		mCtx = ctx;

		mPostService = new PostService();
	}

	public void showHeadView() {
		mTopicPostService.getHashTagResult(new ServiceListResultCallback<HashTag>() {

			@Override
			public void success(List<HashTag> t) {
				mIFeedsView.showTopic(t);
			}

			@Override
			public void failer() {
				mIFeedsView.showTopic(null);
			}
		});
	}

	public FeedsPresenter(IFeedsViewV2 feedViewV2) {
		feedsViewV2 = feedViewV2;
		mPostService = new PostService();
	}

	public void getFeedsData() {
		mPostService.getDataList(new ServiceResultCallback<Post>() {

			@Override
			public void success(List<Post> list) {
				feedsViewV2.setData(list);
			}

			@Override
			public void nomore() {

			}

			@Override
			public void failer() {

			}

			@Override
			public void beforeSuccess() {

			}
		});
	}

}
