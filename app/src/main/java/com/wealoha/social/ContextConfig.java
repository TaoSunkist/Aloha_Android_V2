package com.wealoha.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.setting.PushSettingResult;
import com.wealoha.social.commons.GlobalConstants;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
public class ContextConfig extends PreferenceUtil {

	// public static final ContextConfig mContextConfig = new ContextConfig();
	// 文件名
	public static final String PREFERENCE_NAME = "user_info";
	// 用户信息
	public static final String ID = "userid_sp";
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String BIRTHDAY = "birthday";
	public static final String AGE = "age";
	public static final String HEIGHT = "height";
	public static final String WEIGHT = "weight";
	public static final String T = "t";
	public static final String ME = "me";
	public static final String SUMMARY = "summary";
	public static final String SELFPURPOSES = "selfPurposes";
	public static final String SELFTAG = "selfTag";
	public static final String AVATARIMAGE = "avatarImage";
	public static final String CREATETIMEMILLIS = "createTimeMillis";
	public static final String PROFILEINCOMPLETE = "profileIncomplete";
	public static final String ALOHACOUNT = "alohaCount";
	public static final String ALOHAGETCOUNT = "alohaGetCount";
	public static final String ALOHA = "aloha";
	public static final String MATCH = "match";
	public static final String REGIONCODE = "regionCode";
	public static final String REGION = "region";
	public static final String IMG_WIDTH = "img_width";
	public static final String IMG_HEIGHT = "img_height";
	public static final String IMG_URL = "img_url";
	public static final String IMG_ID = "img_id";
	public static final String IMG_TYPE = "img_type";

	private Context mContext;
	public SharedPreferences mPreferences;

	public ContextConfig() {
		super(PREFERENCE_NAME);
	}

	@Override
	protected Context getContext() {
		return AppApplication.getInstance();
	}

	public void init(Context context) {
		mContext = context;
		mPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	private static class SingletonHolder {

		public static final ContextConfig INSTANCE = new ContextConfig();
	}

	public static ContextConfig getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * @Description:缓存用户信息
	 * @param user
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-10-28
	 */
	public void saveCurrentAccount(Context context, User user) {
		Editor editor = mPreferences.edit();
		editor.putString(ID, user.id);
		editor.putString(TYPE, user.type);
		editor.putString(NAME, user.name);
		editor.putString(BIRTHDAY, user.birthday);
		editor.putString(AGE, user.age);
		editor.putString(HEIGHT, user.height);
		editor.putString(WEIGHT, user.weight);
		editor.putString(REGIONCODE, user.regionCode);
		// editor.putString(REGION, user.region);
		editor.putString(SUMMARY, user.summary);
		editor.putString(SELFTAG, user.selfTag);
		editor.putString(T, user.t);
		editor.putBoolean(ME, user.me);
		// putImage(AVATARIMAGE, user.avatarImage);
		editor.putLong(CREATETIMEMILLIS, user.createTimeMillis);
		editor.putBoolean(PROFILEINCOMPLETE, user.profileIncomplete);
		editor.putInt(ALOHACOUNT, user.alohaCount);
		editor.putInt(ALOHAGETCOUNT, user.alohaGetCount);
		editor.putInt(IMG_WIDTH, user.avatarImage.width);
		editor.putInt(IMG_HEIGHT, user.avatarImage.height);
		editor.putString(IMG_URL, user.avatarImage.url);
		editor.putString(IMG_ID, user.avatarImage.id);
		editor.putString(IMG_TYPE, user.avatarImage.type);
		editor.commit();
	}

	public void getDefaultAccount(Context context, User user) {
		user.id = mPreferences.getString(ID, "");
		user.type = mPreferences.getString(TYPE, "");
		user.name = mPreferences.getString(NAME, "");
		user.birthday = mPreferences.getString(BIRTHDAY, "");
		user.age = mPreferences.getString(AGE, "");
		user.t = mPreferences.getString(T, "");
		user.height = mPreferences.getString(HEIGHT, "");
		user.weight = mPreferences.getString(WEIGHT, "");
		user.regionCode = mPreferences.getString(REGIONCODE, "");
		user.summary = mPreferences.getString(SUMMARY, user.summary);
		user.selfTag = mPreferences.getString(SELFTAG, user.selfTag);
		user.createTimeMillis = mPreferences.getLong(CREATETIMEMILLIS, 0l);
		user.profileIncomplete = mPreferences.getBoolean(PROFILEINCOMPLETE, false);
		user.alohaCount = mPreferences.getInt(ALOHACOUNT, 0);
		user.me = mPreferences.getBoolean(ME, false);
		user.alohaGetCount = mPreferences.getInt(ALOHAGETCOUNT, 0);
		Image headImg = new Image();
		headImg.width = mPreferences.getInt(IMG_WIDTH, 0);
		headImg.height = mPreferences.getInt(IMG_HEIGHT, 0);
		headImg.url = mPreferences.getString(IMG_URL, "");
		headImg.id = mPreferences.getString(IMG_ID, "");
		headImg.type = mPreferences.getString(IMG_TYPE, "");
		user.avatarImage = headImg;
	}

	static public void checkTicket() {

	}

	/**
	 * @Description:是否是第一次进入APP
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-24
	 */
	public boolean isFirstEnter(Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		boolean bool = sp.getBoolean(GlobalConstants.AppConstact.IS_FIRST_ENTER, false);
		if (bool) {
			return true;
		} else {
			Editor editor = sp.edit();
			editor.putBoolean(GlobalConstants.AppConstact.IS_FIRST_ENTER, true);
			editor.commit();
			return false;
		}
	}

	/**
	 * 获取String文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public String getStringWithFilename(String name) {
		return getString(name, "");
	}

	public String getStringWithFilename(String name, String defVlaue) {
		return getString(name, defVlaue);
	}

	/**
	 * 存储int文件
	 * 
	 * @Description:
	 * @param name
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public void putIntWithFilename(String name, int value) {
		putInt(name, value);
	}

	/**
	 * 获取int文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public int getIntWithFilename(String name) {
		return getInt(name, 0);
	}

	/**
	 * 获取int文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public int getIntWithFilename(String name, int defaultValue) {
		return getInt(name, defaultValue);
	}

	/**
	 * 存储long文件
	 * 
	 * @Description:
	 * @param name
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public void putLongWithFilename(String name, long value) {
		putLong(name, value);
	}

	/**
	 * 获取long文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public long getLongWithFilename(String name, long defValue) {
		return getLong(name, defValue);
	}

	/**
	 * 存储float文件
	 * 
	 * @Description:
	 * @param name
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public void putFloatWithFilename(String name, float value) {
		putFloat(name, value);
	}

	/**
	 * 获取float文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public float getFloatWithFilename(String name) {
		return getFloat(name, 0);
	}

	/**
	 * 存储boolean文件
	 * 
	 * @Description:
	 * @param name
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public void putBooleanWithFilename(String name, Boolean value) {
		putBool(name, value);
	}

	/**
	 * 获取boolean文件
	 * 
	 * @Description:
	 * @param name
	 * @return
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public Boolean getBooleanWithFilename(String name) {
		return getBool(name, false);
	}

	public void putPushSetConfig(PushSettingResult pushSettingResult) {
		putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_ENABLE, pushSettingResult.pushEnable);
		putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_SHOW_DETAIL, pushSettingResult.pushShowDetail);
		putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_SOUND, pushSettingResult.pushSound);
		putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_VIBRATION, pushSettingResult.pushVibration);
	}

	public Boolean getBooleanWithDefValue(String name, boolean def) {
		return getBool(name, def);
	}

	/**
	 * 存储string文件
	 * 
	 * @Description:
	 * @param name
	 * @param value
	 * @see:
	 * @since:
	 * @author: sunkist
	 * @date:2014-11-10
	 */
	public void putStringWithFilename(String name, String value) {
		putString(name, value);
	}
}
