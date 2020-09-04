package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ToastUtil;

public class PopularityLockFragment extends BaseFragment implements OnClickListener {

	@Inject
	ServerApi userPromotionService;

	@InjectView(R.id.unlock)
	TextView unlock;
	@InjectView(R.id.title)
	TextView title;
	@InjectView(R.id.back)
	ImageView back;
	private Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_popularity_lock, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bundle = getArguments();
		if (bundle != null) {
			title.setText(getResources().getString(R.string.popular) + " (" + bundle.getInt("alohaGetCount") + ")");
		} else {
			title.setText(getResources().getString(R.string.popular) + " (0)");
		}
	}

	@OnClick({ R.id.unlock, R.id.back })
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.unlock:
			userPromotionService.get(new Callback<ApiResponse<PromotionGetData>>() {

				@Override
				public void success(ApiResponse<PromotionGetData> apiResponse, Response arg1) {
					if (apiResponse == null) {
						return;
					}
					if (apiResponse.isOk()) {
						PromotionGetData r = (PromotionGetData) apiResponse.getData();
						Bundle bundle = new Bundle();
						bundle.putParcelable(PromotionGetData.TAG, r);
						startActivity(GlobalConstants.IntentAction.INTENT_URI_ADVANCEDFEATURED, bundle);
						// contextUtil.getMainAct().Accelerate(null, bundle);
					}
				}

				@Override
				public void failure(RetrofitError arg0) {
					ToastUtil.shortToast(context, getResources().getString(R.string.network_error));
				}
			});
			break;
		case R.id.back:
			getActivity().finish();
			break;
		default:
			break;
		}
	}
}
