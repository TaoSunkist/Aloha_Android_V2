package com.wealoha.social.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-22 上午11:23:48
 * @see <a
 *      href="http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus/15433353#15433353">http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus/15433353#15433353</a>
 */
public class MainThreadBus extends Bus {

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void post(final Object event) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			super.post(event);
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					MainThreadBus.super.post(event);
				}
			});
		}
	}
}
