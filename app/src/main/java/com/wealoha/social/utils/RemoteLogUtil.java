package com.wealoha.social.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import com.wealoha.social.api.ServerApi;
import com.wealoha.social.inject.Injector;

/**
 * 向远程服务器写log
 * 
 * @author hongwei
 * @createTime Jan 12, 2015 12:55:43 PM
 */
public class RemoteLogUtil {

	@Inject
	ServerApi remoteLogService;

	ExecutorService executorService = Executors.newFixedThreadPool(1);

	public RemoteLogUtil() {
		super();
		Injector.inject(this);
	}

	public void log(final String message, final Throwable t) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				String exception = null;

				if (t != null) {
					StringWriter stringWriter = new StringWriter();
					t.printStackTrace(new PrintWriter(stringWriter));
					exception = stringWriter.toString();
				}
				RemoteLogUtil.this.remoteLogService.log(message, exception, System.currentTimeMillis());
			}
		});
	}

	public void log(String message) {
		log(message, null);
	}

	@Override
	protected void finalize() throws Throwable {
		executorService.shutdown();
		super.finalize();
	}
}
