package com.wealoha.social.utils;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.User2;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 临时的新老数据模型转换
 * @copyright wealoha.com
 * @Date:2015年2月28日
 */
public class DockingBeanUtils {

    public static User transUser(User2 apiUser2) {
        User user = User.Companion.fake();
        user.setAge(String.valueOf(apiUser2.getAge()));
        user.setAlohaCount(apiUser2.getAlohaCount());
        user.setAlohaGetCount(apiUser2.getAlohaGetCount());
        user.setBirthday(apiUser2.getBirthday());
        user.setHeight(String.valueOf(apiUser2.getHeight()));
        user.setId(apiUser2.getId());
        user.setName(apiUser2.getName());
        user.setPostCount(apiUser2.getPostCount());
        user.setRegionCode(apiUser2.getRegionCode());
        user.setSelfPurposes(apiUser2.getSelfPurposes());
        user.setSelfTag(apiUser2.getSelfTag());
        user.setSummary(apiUser2.getSummary());
        user.setWeight(String.valueOf(apiUser2.getWeight()));
        user.setZodiac(apiUser2.getZodiac());
        user.setAvatarImage(Image.Companion.fake());
        user.getAvatarImage().setHeight(apiUser2.getAvatarImage().getHeight());
        user.getAvatarImage().setId(apiUser2.getAvatarImage().getImageId());
        user.getAvatarImage().setWidth(apiUser2.getAvatarImage().getWidth());
        user.setHasPrivacy(apiUser2.hasPrivacy());
        user.setAloha(apiUser2.isAloha());
        user.setMatch(apiUser2.isMatch());
        user.setMe(apiUser2.isMe());
        user.setBlock(apiUser2.isBlock());
        user.setProfileIncomplete(apiUser2.isProfileIncomplete());
        return user;
    }
}
