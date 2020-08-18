package com.wealoha.social.commons;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.XL;

/**
 * Created by walker on 14-5-25.
 */
public class FileCacheManager implements CacheManager {

	private Context context;
	private Gson gson;
	private final String TAG = getClass().getName();

	public FileCacheManager(Context context, Gson gson) {
		this.context = context;
		this.gson = gson;
	}

	@Override
	public synchronized <T> void save(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey, T object) {
		File file = context.getFileStreamPath(clazz.getName() + "_" + cacheKey.getName());
		String json = gson.toJson(object);
		FileTools.writeFile(file, json, false);
	}

	public synchronized <T> void saveAppend(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey, T object) {
		File file = context.getFileStreamPath(clazz.getName() + "_" + cacheKey.getName());
		String json = gson.toJson(object);
		FileTools.writeFile(file, json, true);
	}

	public synchronized <T> void clearAppend(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey) {
		File file = context.getFileStreamPath(clazz.getName() + "_" + cacheKey.getName());
		// String json = gson.toJson(object);
		// FileTools.writeFile(file, json, true);
		FileTools.deleteFile(file.getAbsolutePath());
	}

	@Override
	public synchronized <T> T restore(@SuppressWarnings("rawtypes") Class clazz, CacheKey cacheKey) {
		File file = context.getFileStreamPath(clazz.getName() + "_" + cacheKey.getName());
		String content = FileTools.readFile(file);
		try {
			return gson.fromJson(content, cacheKey.getType());
		} catch (JsonSyntaxException e) {
			XL.w(TAG, "無法解析的json:" + content, e);
			return null;
		}
	}

	@Override
	public synchronized <T> void globalSave(CacheKey key, T object) {
		globalSave(key.getName(), object, key.isEncrypt());
	}

	@Override
	public synchronized <T> T globalRestore(CacheKey key) {
		return globalRestore(key.getName(), key.getType(), key.isEncrypt());
	}

	@Override
	public <T> void globalSave(String key, T object) {
		globalSave(key, object, false);
	}

	public <T> void globalSave(String key, T object, boolean encrypt) {
		File file = context.getFileStreamPath(key);
		String json = gson.toJson(object);
		if (encrypt) {
			json = encodeBase64(json);
		}
		FileTools.writeFile(file, json, false);
	}

	@Override
	public <T> T globalRestore(String key, Type typeOfObjct) {
		return globalRestore(key, typeOfObjct, false);
	}

	public <T> T globalRestore(String key, Type typeOfObjct, boolean decrypt) {
		File file = context.getFileStreamPath(key);
		String content = FileTools.readFile(file);
		if (content == null) {
			return null;
		}
		try {
			if (decrypt) {
				content = decodeBase64(content);
			}
			return gson.fromJson(content, typeOfObjct);
		} catch (Exception e) {
			XL.w(TAG, "无法处理的json: " + content, e);
			return null;
		}
	}

	@Override
	public synchronized void globalSave(String key, byte[] data) {
		File file = context.getFileStreamPath(key);
		FileTools.writeBinary(file, data);
	}

	@Override
	public synchronized byte[] globalRestore(String key) {
		File file = context.getFileStreamPath(key);
		return FileTools.readBinary(file);
	}

	private String encodeBase64(String string) {
		if (string == null) {
			return null;
		}
		try {
			return Base64.encodeToString(string.getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String decodeBase64(String string) {
		try {
			return new String(Base64.decode(string, Base64.DEFAULT), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
