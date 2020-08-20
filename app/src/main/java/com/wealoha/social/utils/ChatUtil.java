package com.wealoha.social.utils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.message.ImageMessage;
import com.wealoha.social.beans.message.InboxSession;
import com.wealoha.social.beans.message.InboxSessionResult;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.beans.message.TextMessage;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-19 下午6:28:01
 */
public class ChatUtil {

	private final String TAG = getClass().getSimpleName();

	@Inject
	Context context;
	@Inject
	ServerApi messageService;
	@Inject
	ContextUtil contextUtil;

	public ChatUtil() {
		Injector.inject(this);
	}

	/**
	 * 获取消息的摘要显示
	 * 
	 * @param message
	 * @return
	 */
	public String getMessageSummary(Message message) {
		long t = System.currentTimeMillis();
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).text;
				if (text != null && !"".equals(text)) {
					return text;
				}
			} else if (message instanceof ImageMessage) {
				return context.getString(R.string.chat_message_summary_image);
			}
		} finally {
			XL.d(TAG, "摘要時間: " + (System.currentTimeMillis() - t));
		}
		return null;
	}

	/**
	 * 跳到某个聊天界面(当前用户与指定用户)
	 * 
	 * @param userId
	 *            指定用户的id
	 */
	public void chatWith(String userId) {
		// 如果能够加载到会话,直接跳转,否则创建一个会话再跳转,FIXME 硬编码官方ID---YUZzqAXuczM
		messageService.getInboxSession(userId, new Callback<Result<InboxSessionResult>>() {

			@Override
			public void success(Result<InboxSessionResult> result, Response arg1) {
				if (result == null) {
					return;
				}
				if (result.isOk() && result.getData().list != null && result.getData().list.size() != 0) {
					InboxSession inboxSession;
					Bundle inboxSessionBundle = new Bundle();
					for (InboxSession inboxS : result.getData().list) {
						if (inboxS != null) {
							inboxSession = inboxS;
							inboxSessionBundle.putString("sessionId", inboxSession.id);
							inboxSessionBundle.putParcelable("toUser", inboxSession.user);
							contextUtil.getMainAct().startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
							break;
						}
					}
				} else {
					ToastUtil.shortToast(context, R.string.is_not_work);
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				Log.d("FAQ", "获取会话失败" + arg0.getCause().getMessage());
			}
		});
	}

	public void bySessionIdToDialogue(String sessionId) {

		messageService.post(sessionId, new Callback<Result<InboxSessionResult>>() {

			@Override
			public void failure(RetrofitError arg0) {
				Log.d("FAQ", "创建会话失败");
			}

			@Override
			public void success(Result<InboxSessionResult> result, Response arg1) {
				if (result == null || !result.isOk()) {
					return;
				} else if (result.getData().list != null) {
					InboxSession inboxSession;
					Bundle inboxSessionBundle = new Bundle();
					for (InboxSession inboxS : result.getData().list) {
						if (inboxS != null) {
							inboxSession = inboxS;
							inboxSessionBundle.putString("sessionId", inboxSession.id);
							inboxSessionBundle.putParcelable("toUser", inboxSession.user);
							if (contextUtil.getForegroundAct() != null) {
								((BaseFragAct) contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
							}
							break;
						}
					}
				} else {
					ToastUtil.shortToast(context, R.string.is_not_work);
				}
			}
		});

	}
}
