package com.wealoha.social;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wealoha.social.utils.XL;

/**
 * 实例单例工厂
 * 
 * @Description:
 * @see:
 * @since:
 * @Date:2014-6-19
 */
public class SingTonInstance {

	public static interface InsanceFactory<T> {

		public T createInstance();
	}

	private static final String TAG = SingTonInstance.class.getSimpleName();

	private static Map<Object, Object> all = new ConcurrentHashMap<Object, Object>(32);

	/**
	 * 单例的简单变种实现 给一个类型, 返回一个实例, 如果这个实例以前没有创建过, 则会使用默认的构造函数创建一个实例.
	 * 
	 * @param <T>
	 * @param cls
	 * @return
	 */
	public static <T> T getInstance(Class<T> cls) {
		return getInstance(cls, cls, null);
	}

	/**
	 * 单例，需要提供一个key, 以这个key获取的会是同一个实例
	 * 
	 * @Description:
	 * @param cls
	 * @param key
	 * @return
	 * @see:
	 * @since:
	 * @author: huangyx2
	 * @date:2013-7-26
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> cls, String key) {
		Object obj = all.get(cls.getName() + "|" + key);
		if (obj == null) {
			synchronized (cls) {
				obj = all.get(cls.getName() + "|" + key);
				if (obj == null) {
					try {
						obj = cls.newInstance();
						all.put(cls.getName() + "|" + key, obj);
					} catch (Exception e) {
						XL.e(TAG, e.toString());
					}
				}
			}
		}
		return (T) obj;
	}

	/**
	 * 工厂支持
	 * 
	 * @param <T>
	 * @param superCls
	 *            父类/抽象类或接口
	 * @param objCls
	 *            子类或实现类
	 * @return 返回父类/抽象类或接口
	 */
	public static <T> T getInstance(Class<T> superCls, Class<? extends T> objCls) {
		return getInstance(superCls, objCls, null);
	}

	public static <T> T getInstance(Class<T> objCls, InsanceFactory<T> instanceFactory) {
		return getInstance(objCls, objCls, instanceFactory);
	}

	/**
	 * 支持工厂的单例
	 * 
	 * @param <T>
	 * @param superCls
	 *            父类/抽象类或接口
	 * @param objCls
	 *            子类或实现类
	 * @param instEvent
	 *            创建实例时的回调
	 * @return 返回父类/抽象类或接口
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> superCls, Class<? extends T> objCls, InsanceFactory<T> instanceFactory) {
		Object obj = all.get(superCls);
		if (obj == null) {
			synchronized (superCls) {
				obj = all.get(superCls);
				if (obj == null) {
					try {
						if (instanceFactory != null) {
							obj = instanceFactory.createInstance();
						} else {
							obj = objCls.newInstance();
						}
						all.put(superCls, obj);

					} catch (Exception e) {
						XL.e(TAG, e.toString());
					}
				}
			}
		}
		return (T) obj;
	}

	/**
	 * 设置一个实例, 下次可以通过getInstance来获取
	 * 
	 * @param <T>
	 * @param obj
	 */
	synchronized public static <T> void setInstance(T obj) {
		assert (obj != null);
		if (obj != null) {
			all.put(obj.getClass(), obj);
		}
	}

	synchronized public static <T> void removeInstance(Class<T> cls) {
		all.remove(cls);
	}

	/**
	 * 工厂模式支持
	 * 
	 * @param <T>
	 * @param superCls
	 *            接口类型/抽象类/父类
	 * @param obj
	 *            实例/子类
	 */
	synchronized public static <T> void setInstance(Class<? super T> superCls, T obj) {
		assert (obj != null && superCls != null);
		if (superCls != null && obj != null) {
			all.put(superCls, obj);
		}
	}
}
