package com.wealoha.social.commons;

import com.wealoha.social.commons.GlobalConstants.CacheKey;

/**
 * 支持Cache
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午12:31:37
 */
public interface HasCache {

	/**
	 * 缓存数据
	 * 
	 * @param key
	 *            在当前界面下唯一，相同的key在不同的Activity/Fragment下不会冲突
	 * @param object
	 */
	public <T> void save(CacheKey key, T object);

	public <T> T restore(CacheKey key);

	public <T> void saveAppend(CacheKey key, T object);

	public <T> void clearAppend(CacheKey cacheKey);

	/**
	 * 缓存数据
	 * 
	 * @param key
	 *            全局唯一
	 * @param object
	 */
	public <T> void globalSave(CacheKey key, T object);

	public <T> T globalRestore(CacheKey key);
}
