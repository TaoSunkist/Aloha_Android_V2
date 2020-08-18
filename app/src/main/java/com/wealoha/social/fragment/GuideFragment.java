package com.wealoha.social.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.wealoha.social.R;
import com.wealoha.social.utils.ToastUtil;

public class GuideFragment extends Fragment {

	private RelativeLayout guide_nav_bg_rl;
	int position;

	public GuideFragment(int[] resId) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_guide_nav, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			position = bundle.getInt("position");
		}
		ToastUtil.shortToast(getActivity(), position + "");
		guide_nav_bg_rl = (RelativeLayout) view.findViewById(R.id.guide_nav_bg_rl);
		guide_nav_bg_rl.setBackgroundResource(R.drawable.guide_three);
	}

}
