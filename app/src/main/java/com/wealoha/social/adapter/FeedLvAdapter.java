package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.User;
import com.wealoha.social.cache.ImageRender;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.utils.ContextUtil;

public class FeedLvAdapter extends BaseAdapter implements OnClickListener {

	private List<Feed> mFeedList;
	private final LayoutInflater mInflater;
	public static BitmapUtils bitmapUtils;
	public Map<String, User> mUserList;

	private List<ImageView> mFeedPraiseList;

	@Inject
	ImageRender imageRender;

	@Inject
	ContextUtil contextUtil;
	@Inject
	Context context;

	public FeedLvAdapter(List<Feed> mFeedList, Map<String, User> mUserList, Map<String, Integer> commentCountMap, Map<String, Integer> likeCountMap) {
		this.mFeedList = mFeedList;
		mInflater = LayoutInflater.from(context);
		this.mUserList = mUserList;
		Injector.inject(this);
		mFeedPraiseList = new ArrayList<ImageView>();
		for (int i = 0; i < mFeedList.size(); i++) {
			mFeedPraiseList.add(null);
		}
	}

	@Override
	public int getCount() {
		if (mFeedList != null && mFeedList.size() > 0) {
			return mFeedList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mFeedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_feed, parent, false);
			// convertView.setTag(feedholder);
		} else {
			// feedholder = (FeedHolder) convertView.getTag();
		}
		return convertView;
	}

	/**
	 * @Title: praiseUser
	 * @Description: 赞
	 * @param @param postId 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void praiseFeed(String postId) {
		RequestParams requestParams = new RequestParams();
		contextUtil.addGeneralHttpHeaders(requestParams);
		requestParams.addBodyParameter("postId", postId);
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.PRAISE_USER_FEED, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// ToastUtil.shortToast(contextUtil.getMainAct(), arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// ToastUtil.shortToast(mMainAct, arg0.result);
				Log.i("ALOHA_FEED_ADAPTER", arg0.result);
			}
		});
	}

	public void dislikeFeed(String postId) {
		RequestParams requestParams = new RequestParams();
		contextUtil.addGeneralHttpHeaders(requestParams);
		requestParams.addBodyParameter("postId", postId);
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.DISLIKE_USER_FEED, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// ToastUtil.shortToast(contextUtil.getMainAct(), arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// ToastUtil.shortToast(mMainAct, arg0.result);
				Log.i("ALOHA_FEED_ADAPTER", arg0.result);
			}
		});
	}

	/**
	 * 
	 * @param mFeedList
	 * @param mUserList
	 * @deprecated 使用 {@link #notifyDataChange(List, Map, Map, Map)}
	 */
	public void notifyDataChange(List<Feed> mFeedList, Map<String, User> mUserList) {
		this.mFeedList = mFeedList;
		this.mUserList = mUserList;
		this.notifyDataSetChanged();
	}

	public void notifyDataChange(List<Feed> mFeedList, Map<String, User> mUserList, Map<String, Integer> commentCountMap, Map<String, Integer> likeCountMap) {
		this.mFeedList = mFeedList;
		this.mUserList = mUserList;
		this.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
