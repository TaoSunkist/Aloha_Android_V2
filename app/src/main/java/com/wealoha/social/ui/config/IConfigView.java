package com.wealoha.social.ui.config;

import android.view.View.OnClickListener;

import androidx.loader.app.LoaderManager;

import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.instagram.InstagramResult;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;

public interface IConfigView extends OnClickListener, //
		LoaderManager.LoaderCallbacks<ApiResponse<PromotionGetData>>,//
		ListItemCallback {
	public void clearCacheSuccess();

	public void startUpdateApp();

	public void updateDialog(String string);
	public void setUiShowCacheSize(String cacheSize);

	public void refreshInstagramSuccess(ApiResponse<InstagramResult> apiResponse);
}
