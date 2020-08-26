package com.wealoha.social.utils;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.User;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 临时的新老数据模型转换
 * @copyright wealoha.com
 * @Date:2015年2月28日
 */
public class DockingBeanUtils {

    public static User transUser(User apiUser) {
        User user = User.Companion.fake();
        user.setAge(apiUser.getAge());
        user.setAlohaCount(apiUser.getAlohaCount());
        user.setAlohaGetCount(apiUser.getAlohaGetCount());
        user.setBirthday(apiUser.getBirthday());
        user.setHeight(apiUser.getHeight());
        user.setId(apiUser.getId());
        user.setName(apiUser.getName());
        user.setPostCount(apiUser.getPostCount());
        user.setRegionCode(apiUser.getRegionCode());
        user.setSelfPurposes(apiUser.getSelfPurposes());
        user.setSelfTag(apiUser.getSelfTag());
        user.setSummary(apiUser.getSummary());
        user.setWeight(apiUser.getWeight());
        user.setZodiac(apiUser.getZodiac());
        user.setAvatarImage(Image.Companion.fake());
        user.getAvatarImage().setHeight(apiUser.getAvatarImage().getHeight());
        user.getAvatarImage().setId(apiUser.getAvatarImage().getId());
        user.getAvatarImage().setWidth(apiUser.getAvatarImage().getWidth());
        user.setHasPrivacy(apiUser.hasPrivacy());
        user.setAloha(apiUser.getAloha());
        user.setMatch(apiUser.getAloha());
        user.setMe(apiUser.getMe());
        user.setBlock(apiUser.getBlock());
        user.setProfileIncomplete(apiUser.getProfileIncomplete());
        return user;
    }
}
