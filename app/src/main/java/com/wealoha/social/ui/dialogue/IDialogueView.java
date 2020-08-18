package com.wealoha.social.ui.dialogue;

import android.app.LoaderManager.LoaderCallbacks;
import android.view.View.OnTouchListener;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.message.InboxMessageResult;

/**
 * @author sunkist
 */
public interface IDialogueView extends LoaderCallbacks<Result<InboxMessageResult>>, OnTouchListener {
	void initData();
	void initDataError();
	void clearUnreadCount(String sessionId);
	void changeBackKeyUI(int unreadCount);
	void byNoticeJoinDialog();
	void designUi();
	void showMatchHint();
}
