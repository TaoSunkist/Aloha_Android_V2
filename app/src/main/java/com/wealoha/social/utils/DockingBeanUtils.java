package com.wealoha.social.utils;

import com.wealoha.social.beans.Image;
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

    public static User transUser(com.wealoha.social.api.user.bean.User apiUser) {
        User user = User.Companion.fake();
        user.setAge(String.valueOf(apiUser.getAge()));
        user.setAlohaCount(apiUser.getAlohaCount());
        user.setAlohaGetCount(apiUser.getAlohaGetCount());
        user.setBirthday(apiUser.getBirthday());
        user.setHeight(String.valueOf(apiUser.getHeight()));
        user.setId(apiUser.getId());
        user.setName(apiUser.getName());
        user.setPostCount(apiUser.getPostCount());
        user.setRegionCode(apiUser.getRegionCode());
        user.setSelfPurposes(apiUser.getSelfPurposes());
        user.setSelfTag(apiUser.getSelfTag());
        user.setSummary(apiUser.getSummary());
        user.setWeight(String.valueOf(apiUser.getWeight()));
        user.setZodiac(apiUser.getZodiac());
        user.setAvatarImage(Image.Companion.fake());
        user.getAvatarImage().setHeight(apiUser.getAvatarImage().getHeight());
        user.getAvatarImage().setId(apiUser.getAvatarImage().getImageId());
        user.getAvatarImage().setWidth(apiUser.getAvatarImage().getWidth());
        user.setHasPrivacy(apiUser.hasPrivacy());
        user.setAloha(apiUser.isAloha());
        user.setMatch(apiUser.isMatch());
        user.setMe(apiUser.isMe());
        user.setBlock(apiUser.isBlock());
        user.setProfileIncomplete(apiUser.isProfileIncomplete());
        return user;
    }
}
