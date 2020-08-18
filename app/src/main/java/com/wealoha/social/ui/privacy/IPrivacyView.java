package com.wealoha.social.ui.privacy;

import android.widget.SeekBar.OnSeekBarChangeListener;

import com.wealoha.social.view.custom.SlideSwitch.OnSwitchChangedListener;

public interface IPrivacyView extends OnSwitchChangedListener, OnSeekBarChangeListener {
	public void getPrivacyRange(boolean isSuccess, int invisibleRange);
	public void setPrivacyRange(int privacyRange);
}
