package com.wealoha.social.push.model;

import javax.inject.Inject;

import android.app.Activity;

import com.wealoha.social.AppApplication;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.event.NewCommentPushEvent;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.PushType;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.push.notification.PostLikeNotification;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.Utils;

public class PostLikePush extends BasePush implements NotifyPush<PostLikeNotification> {
	@Inject
	ContextUtil contextUtil;

	public PostLikePush(AppApplication instance) {
		super();
		mCtx = instance;
	}

	private String ALOHA_PUSH_Notification_Post_Like = "ALOHA_PUSH_Notification_Post_Like";

	@Override
	public void sendNotification(PostLikeNotification notification) {
		NotificationCount.setCommentCount();
		if (Utils.isApplicationBroughtToBackground(AppApplication.getInstance())) {// 在后台
			String msg = null;
			if (ALOHA_PUSH_Notification_Post_Like.equals(notification.getAlertLocKey())) {
				// 点赞的通知
				msg = mCtx.getString(R.string.aloha_push_notification_post_like);
			} else if (PushType.ALOHA_PUSH_Notification_Post_Like_NoDetail.getPushType().equals(notification.getAlertLocKey())) {
				// 点赞的通知：不顯示詳情
				msg = mCtx.getString(R.string.aloha_push_notification_post_nodetail);
			}
			NoticeBarController.getInstance(mCtx).showNewLikeNotice(mCtx, notification, msg);
		} else {
			Activity act = contextUtil.getForegroundAct();
			if (act == null) {
				return;
			}
			if (act instanceof MainAct) {
				((MainAct) act).busSentEvent(new NewCommentPushEvent());
			}
		}
	}
}
