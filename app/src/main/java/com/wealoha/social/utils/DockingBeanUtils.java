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
		User user = new User();
		user.age = String.valueOf(apiUser.getAge());
		user.alohaCount = apiUser.getAlohaCount();
		user.alohaGetCount = apiUser.getAlohaGetCount();
		user.birthday = apiUser.getBirthday();
		user.height = String.valueOf(apiUser.getHeight());
		user.id = apiUser.getId();
		user.name = apiUser.getName();
		user.postCount = apiUser.getPostCount();
		user.regionCode = apiUser.getRegionCode();
		user.selfPurposes = apiUser.getSelfPurposes();
		user.selfTag = apiUser.getSelfTag();
		user.summary = apiUser.getSummary();
		user.weight = String.valueOf(apiUser.getWeight());
		user.zodiac = apiUser.getZodiac();
		user.avatarImage = new Image();
		user.avatarImage.height = apiUser.getAvatarImage().getHeight();
		user.avatarImage.id = apiUser.getAvatarImage().getImageId();
		user.avatarImage.width = apiUser.getAvatarImage().getWidth();
		user.hasPrivacy = apiUser.hasPrivacy();
		user.aloha = apiUser.isAloha();
		user.match = apiUser.isMatch();
		user.me = apiUser.isMe();
		user.block = apiUser.isBlock();
		user.profileIncomplete = apiUser.isProfileIncomplete();
		return user;
	}
}
