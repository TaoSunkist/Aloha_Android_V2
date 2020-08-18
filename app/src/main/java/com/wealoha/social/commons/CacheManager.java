package com.wealoha.social.commons;

import java.lang.reflect.Type;

import com.wealoha.social.commons.GlobalConstants.CacheKey;

/**
 * Created by walker on 14-5-25.
 */
public interface CacheManager {

	/**
	 * 根据类做缓存
	 * 
	 * @param clazz
	 * @param cacheKey
	 * @param object
	 */
	public <T> void save(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey, T object);

	public <T> T restore(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey);

	public <T> void saveAppend(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey, T object);

	public <T> void clearAppend(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey);

	/**
	 * 默认保存成json
	 * 
	 * @param key
	 * @param object
	 * @param <T>
	 */
	public <T> void globalSave(CacheKey key, T object);

	/**
	 * 默认按照json读取
	 * 
	 * @param key
	 * @param typeOfT
	 * @param <T>
	 * @return
	 */
	public <T> T globalRestore(CacheKey key);

	public <T> void globalSave(String key, T object);

	public <T> T globalRestore(String key, Type typeOfObjct);

	public <T> void globalSave(String key, byte[] data);

	public byte[] globalRestore(String key);
}
