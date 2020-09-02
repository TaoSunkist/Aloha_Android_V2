package com.wealoha.social.ui.dialogue;

import android.view.View.OnTouchListener;

import androidx.loader.app.LoaderManager;

import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.message.InboxMessageResult;

/**
 * @author sunkist
 */
public interface IDialogueView extends LoaderManager.LoaderCallbacks<ApiResponse<InboxMessageResult>>, OnTouchListener {
	void initData();
	void initDataError();
	void clearUnreadCount(String sessionId);
	void changeBackKeyUI(int unreadCount);
	void byNoticeJoinDialog();
	void designUi();
	void showMatchHint();
}
