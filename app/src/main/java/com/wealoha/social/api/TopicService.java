package com.wealoha.social.api;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;

import com.wealoha.social.AppApplication;
import com.wealoha.social.beans.HashTag;
import com.wealoha.social.beans.HashTagDTO;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.TopicData;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.AMapUtil;
import com.wealoha.social.utils.AMapUtil.LocationCallback;

public class TopicService implements BaseService<HashTagDTO> {

	@Inject
	ServerApi topicAPI;
	@Inject
	Context context;

	AppApplication app;

	public TopicService() {
		Injector.inject(this);
		app = AppApplication.getInstance();
	}

	public void getTopic(final ServiceResultCallback<HashTag> callback) {
		new AMapUtil().getLocation(context, new LocationCallback() {

			@Override
			public void locaSuccess() {
				getTopicFromServer(app.locationXY[0], app.locationXY[1], callback);
			}

			@Override
			public void locaError() {
				getTopicFromServer(app.locationXY[0], app.locationXY[1], callback);
			}
		});
	}

	public void getTopicFromServer(Double latitude, Double longitude, final ServiceResultCallback<HashTag> callback) {
		topicAPI.getTopic(latitude, longitude, new Callback<Result<TopicData>>() {

			@Override
			public void success(Result<TopicData> result, Response arg1) {
				transHashTagDTO2HashTag(result.data.tag);
				// callback.
			}

			@Override
			public void failure(RetrofitError arg0) {

			}
		});

	}

	private void transHashTagDTO2HashTag(List<HashTagDTO> htd) {
	}
}
