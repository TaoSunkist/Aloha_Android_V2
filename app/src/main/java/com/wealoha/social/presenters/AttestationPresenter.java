package com.wealoha.social.presenters;

import javax.inject.Inject;

import android.content.Context;

import com.wealoha.social.api.User2Service;
import com.wealoha.social.callback.CallbackImpl;
import com.wealoha.social.ui.attestation.IAttestationView;

public class AttestationPresenter extends AbsPresenter {

	private IAttestationView mIAttestationView;
	@Inject
	Context mContext;
	@Inject
	User2Service mUser2Service;

	public AttestationPresenter(IAttestationView iAttestationView) {
		mIAttestationView = iAttestationView;
	}

	public void verifyPassword(String pw) {
		mUser2Service.verifyPassword(pw, new CallbackImpl() {

			@Override
			public void success() {
				mIAttestationView.passwordRight();
			}

			@Override
			public void failure() {
				mIAttestationView.passwordError();
//				ToastUtil.longToast(mContext, R.string.network_error);
			}
		});
	}
}
