package com.wealoha.social.push.model;

import javax.inject.Inject;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.wealoha.social.inject.Injector;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.utils.ContextUtil;

public abstract class BasePush {

	protected Push2Type push2Type;
	protected Context mCtx;
	protected NotificationCompat.Builder mNotifyBuilder;
	protected NotificationManager mNotificationManager;
	@Inject
	ContextUtil contextUtil;
	static int i = 0;

	protected BasePush() {
		Injector.inject(this);
	}
}
