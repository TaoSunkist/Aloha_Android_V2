package com.wealoha.social.ui.dialogue;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.beans.User;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil.Font;

/**
 * 
 * @author sunkist
 *
 */
public class DialoguePresenter {

	private DialogueHolder mDialogueHolder;
	private IDialogueView mIDialogueView;

	public DialoguePresenter(IDialogueView iDialogueView) {
		mIDialogueView = iDialogueView;
		mDialogueHolder = new DialogueHolder();
	}

	public void initData(Bundle bundle) {
		if (bundle != null) {
			mDialogueHolder.setSessionId(bundle.getString("sessionId"));
			mDialogueHolder.setToUser((User) bundle.get("toUser"));
			mDialogueHolder.setForceBackToSessionList(bundle.getBoolean("forceBackToSessionList", false));
			int unreadCount = bundle.getInt("unreadCount", 0);
			if (mDialogueHolder.isForceBackToSessionList()) {// 从push 页进入要开启手势锁
				ContextConfig.getInstance().putBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY, true);
			}
			mIDialogueView.clearUnreadCount(mDialogueHolder.getSessionId());
			mIDialogueView.changeBackKeyUI(unreadCount);
			if (mDialogueHolder.getToUser() == null) {
				mIDialogueView.byNoticeJoinDialog();
			} else {
				mIDialogueView.designUi();
			}

			mDialogueHolder.setShowMatchHint(bundle.getBoolean("showMatchHint", false));
			if (mDialogueHolder.isShowMatchHint()) {
				mIDialogueView.showMatchHint();
			}
			mIDialogueView.initData();
		} else {
			mIDialogueView.initDataError();
		}

	}

	/**
	 * 获取会话页面的数据持有者
	 */
	public DialogueHolder getDialogueHolder() {
		return mDialogueHolder;
	}
}
