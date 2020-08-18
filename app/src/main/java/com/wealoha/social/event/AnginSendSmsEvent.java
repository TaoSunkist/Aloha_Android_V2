package com.wealoha.social.event;

import com.wealoha.social.adapter.ChatMsgViewAdapter;

public class AnginSendSmsEvent {

	private ChatMsgViewAdapter.ViewHolder mViewHolder;

	public AnginSendSmsEvent(ChatMsgViewAdapter.ViewHolder viewHolder) {
		mViewHolder = viewHolder;
	}

	public ChatMsgViewAdapter.ViewHolder getViewHolder() {
		return mViewHolder;
	}
}
