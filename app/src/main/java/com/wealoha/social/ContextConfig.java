package com.wealoha.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.PushSettingResult;
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
     * @param user
     * @Description:缓存用户信息
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-28
     */
    public void saveCurrentAccount(Context context, User user) {
        Editor editor = mPreferences.edit();
        editor.putString(ID, user.getId());
        editor.putString(TYPE, user.getType());
        editor.putString(NAME, user.getName());
        editor.putString(BIRTHDAY, user.getBirthday());
        editor.putInt(AGE, user.getAge());
        editor.putInt(HEIGHT, user.getHeight());
        editor.putInt(WEIGHT, user.getWeight());
        editor.putString(REGIONCODE, user.getRegionCode());
        // editor.putString(REGION, user.region);
        editor.putString(SUMMARY, user.getSummary());
        editor.putString(SELFTAG, user.getSelfTag());
        editor.putString(T, user.getType());
        editor.putBoolean(ME, user.getMe());
        // putImage(AVATARIMAGE, user.avatarImage);
        editor.putLong(CREATETIMEMILLIS, user.getCreateTimeMillis());
        editor.putBoolean(PROFILEINCOMPLETE, user.getProfileIncomplete());
        editor.putInt(ALOHACOUNT, user.getAlohaCount());
        editor.putInt(ALOHAGETCOUNT, user.getAlohaGetCount());
        editor.putInt(IMG_WIDTH, user.getAvatarImage().getWidth());
        editor.putInt(IMG_HEIGHT, user.getAvatarImage().getHeight());
        editor.putString(IMG_URL, user.getAvatarImage().getUrl());
        editor.putString(IMG_ID, user.getAvatarImage().getId());
        editor.putString(IMG_TYPE, user.getAvatarImage().getType());
        editor.commit();
    }

    public void getDefaultAccount(Context context, User user) {
        user.setId(mPreferences.getString(ID, ""));
        user.setType(mPreferences.getString(TYPE, ""));
        user.setName(mPreferences.getString(NAME, ""));
        user.setBirthday(mPreferences.getString(BIRTHDAY, ""));
        user.setAge(mPreferences.getInt(AGE, 0));
        user.setHeight(mPreferences.getInt(HEIGHT, 0));
        user.setWeight(mPreferences.getInt(WEIGHT, 0));
        user.setRegionCode(mPreferences.getString(REGIONCODE, ""));
        user.setSummary(mPreferences.getString(SUMMARY, user.getSummary()));
        user.setSelfTag(mPreferences.getString(SELFTAG, user.getSelfTag()));
        user.setCreateTimeMillis(mPreferences.getLong(CREATETIMEMILLIS, 0l));
        user.setProfileIncomplete(mPreferences.getBoolean(PROFILEINCOMPLETE, false));
        user.setAlohaCount(mPreferences.getInt(ALOHACOUNT, 0));
        user.setMe(mPreferences.getBoolean(ME, false));
        user.setAlohaGetCount(mPreferences.getInt(ALOHAGETCOUNT, 0));
        Image headImg = Image.Companion.fake();
        headImg.setWidth(mPreferences.getInt(IMG_WIDTH, 0));
        headImg.setHeight(mPreferences.getInt(IMG_HEIGHT, 0));
        headImg.setUrl(mPreferences.getString(IMG_URL, ""));
        headImg.setId(mPreferences.getString(IMG_ID, ""));
        headImg.setType(mPreferences.getString(IMG_TYPE, ""));
        user.setAvatarImage(headImg);
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @param value
     * @Description:
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @param value
     * @Description:
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @param value
     * @Description:
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @param value
     * @Description:
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
     * @param name
     * @return
     * @Description:
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
     * @param name
     * @param value
     * @Description:
     * @see:
     * @since:
     * @author: sunkist
     * @date:2014-11-10
     */
    public void putStringWithFilename(String name, String value) {
        putString(name, value);
    }
}
