package com.wealoha.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 文件存储公共类
 * @copyright wealoha.com
 * @Date:2014-10-28
 */
public abstract class PreferenceUtil {

	protected SharedPreferences sp;

	protected abstract Context getContext();

	public PreferenceUtil() {

	}

	public PreferenceUtil(String name) {
		sp = getContext().getSharedPreferences(name, 0);
	}

	public Editor edit() {
		return sp.edit();
	}

	/**
	 * @Description:
	 * @param key
	 * @param value
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-10-28
	 */
	protected void putBool(String key, boolean value) {
		Editor ed = sp.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	/**
	 * get boolean value
	 * 
	 * @Description:
	 * @param key
	 * @param defValue
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	public boolean getBool(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}

	/**
	 * put int value
	 * 
	 * @Description:
	 * @param key
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected void putInt(String key, int value) {
		Editor ed = sp.edit();
		ed.putInt(key, value);
		ed.commit();
	}

	/**
	 * get int value
	 * 
	 * @Description:
	 * @param key
	 * @param defValue
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	/**
	 * put long value
	 * 
	 * @Description:
	 * @param key
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected void putLong(String key, long value) {
		Editor ed = sp.edit();
		ed.putLong(key, value);
		ed.commit();
	}

	/**
	 * get long value
	 * 
	 * @Description:
	 * @param key
	 * @param defValue
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}

	/**
	 * put string value
	 * 
	 * @Description:
	 * @param key
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected void putString(String key, String value) {
		Editor ed = sp.edit();
		ed.putString(key, value);
		ed.commit();
	}

	/**
	 * get string value
	 * 
	 * @Description:
	 * @param key
	 * @param defValue
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	/**
	 * put float value
	 * 
	 * @Description:
	 * @param key
	 * @param value
	 * @see:
	 * @since:
	 * @author: huangyx2
	 * @date:2013-7-24
	 */
	protected void putFloat(String key, float value) {
		Editor ed = sp.edit();
		ed.putFloat(key, value);
		ed.commit();
	}

	/**
	 * get float value
	 * 
	 * @Description:
	 * @param key
	 * @param defValue
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2013-7-24
	 */
	protected float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}

	protected void cleanUserConfig() {
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}
