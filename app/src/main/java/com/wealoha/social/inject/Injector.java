package com.wealoha.social.inject;

import com.wealoha.social.utils.XL;

import dagger.ObjectGraph;

/**
 * 注入的上下文和帮助工具<br/>
 * 
 * 未托管的模块需要注入直接调用即可 {@link Injector#init(Object)}
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-28 下午3:19:36
 */
public final class Injector {

	private static final String TAG = Injector.class.getSimpleName();

	protected Injector() {
	}

	public static ObjectGraph objectGraph = null;

	public static void init(final Object rootModule) {

		XL.d(TAG, "初始化依赖注入");

		if (objectGraph == null) {
			objectGraph = ObjectGraph.create(rootModule);
		} else {
			objectGraph = objectGraph.plus(rootModule);
		}

		// Inject statics
		objectGraph.injectStatics();

	}

	public static final void inject(final Object target) {
		objectGraph.inject(target);
	}

	public static <T> T resolve(Class<T> type) {
		return objectGraph.get(type);
	}
}
