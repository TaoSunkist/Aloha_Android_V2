package com.wealoha.social.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wealoha.social.beans.Image;

/**
 * 存放ImageMap,根据ImageId取图片地址
 * 
 * @author hongwei
 * @createTime Jan 12, 2015 4:07:46 PM
 */
public class ImageCache {

	// TODO 待优化
	private static Map<String, Image> imageMapCache = new ConcurrentHashMap<String, Image>();

	// .maximumSize(2000) //
	// .expireAfterWrite(3, TimeUnit.DAYS) //
	// .build();

	public static Image getImageFromMap(String imageId) {
		return imageMapCache.get(imageId);
	}

	public static Map<String, Image> getImageByKeys(Collection<String> keys) {
		Map<String, Image> result = new HashMap<String, Image>();
		for (String key : keys) {
			result.put(key, imageMapCache.get(keys));
		}
		return result;
	}

	public static void setToCache(Map<String, Image> map) {
		if (map == null || map.size() == 0) {
			return;
		}
		imageMapCache.putAll(map);
	}
}
