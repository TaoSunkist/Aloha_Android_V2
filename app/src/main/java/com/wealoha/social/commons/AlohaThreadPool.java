package com.wealoha.social.commons;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wealoha.social.utils.XL;

/**
 * 新闻异步线程池
 * 
 * @Description:
 * @author:sunkist
 * @see:
 * @since:
 * @copyright © jrzj.com
 * @Date:2014-5-16
 */
public class AlohaThreadPool {

	public enum ENUM_Thread_Level {
		TL_common, TL_AtOnce
	}

	private final static int I_COMMON_POOL_NUM = 10;
	private final static int I_ACCOUNT_POOL_NUM = 5;
	private final static int MAX_POLL_COUNT = 100;
	private static final String TAG = "NewsThreadPool";
	// 普通任务线程
	public static ThreadPoolExecutor poolInstance_common;
	// UI线程用
	public static ThreadPoolExecutor poolInstance_AtOnce = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

	private AlohaThreadPool() {

	}

	/**
	 * 调用
	 * 
	 * @Description:
	 * @param e_t_l
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-5-16
	 */
	public static ThreadPoolExecutor getInstance(ENUM_Thread_Level e_t_l) {
		switch (e_t_l) {
		case TL_common:// 普通任务线程
			return getInst_CommonPool();
		case TL_AtOnce:// UI的线程
			return getInst_AtOncePool();
		default:
			return getInst_CommonPool();
		}

	}

	/**
	 * 
	 * 
	 * 普通任务
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-5-16
	 */
	private static ThreadPoolExecutor getInst_CommonPool() {
		try {
			if (poolInstance_common == null || poolInstance_common.isShutdown()) {
				poolInstance_common = (ThreadPoolExecutor) Executors.newFixedThreadPool(I_COMMON_POOL_NUM);
				poolInstance_common.setKeepAliveTime(10, TimeUnit.SECONDS);
				poolInstance_common.allowCoreThreadTimeOut(true);
				poolInstance_common.setMaximumPoolSize(MAX_POLL_COUNT);
			}
			// XL.v("xianchedngsf", "poolInstance_commons : " +
			// poolInstance_common.getPoolSize());
		} catch (Exception e) {
			// XL.e(TAG, "线程池出错: " + e.toString());
		}
		return poolInstance_common;
	}

	/**
	 * UI线程用
	 * 
	 * @Description:
	 * @return
	 * @see:
	 * @since:n
	 * @author: sunkist
	 * @date:2014-5-16
	 */

	private static ThreadPoolExecutor getInst_AtOncePool() {
		try {
			if (poolInstance_AtOnce == null || poolInstance_AtOnce.isShutdown()) {
				poolInstance_AtOnce = (ThreadPoolExecutor) Executors.newFixedThreadPool(I_ACCOUNT_POOL_NUM);
			} else if (poolInstance_AtOnce != null) {//
				poolInstance_AtOnce.shutdownNow();
				poolInstance_AtOnce = (ThreadPoolExecutor) Executors.newFixedThreadPool(I_ACCOUNT_POOL_NUM);
			} else {
				poolInstance_AtOnce = (ThreadPoolExecutor) Executors.newFixedThreadPool(I_ACCOUNT_POOL_NUM);
			}
			poolInstance_AtOnce.setMaximumPoolSize(MAX_POLL_COUNT);
			// XL.v("xianchedngsf", "poolInstance_AtOnce : " + poolInstance_AtOnce.getPoolSize());
		} catch (Exception e) {
			XL.e(TAG, "线程池出错: " + e.toString());
		}
		return poolInstance_AtOnce;
	}

}
