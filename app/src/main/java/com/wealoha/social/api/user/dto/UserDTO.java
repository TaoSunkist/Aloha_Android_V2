package com.wealoha.social.api.user.dto;

import java.util.List;

import com.wealoha.social.api.common.dto.ImageDTO;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午11:21:56
 */
public class UserDTO {

	public String id;
	public String name;
	public String birthday; // yyyy-MM-dd
	public int age;
	public int height; // 1+
	public int weight; // 1+
	/** 我当前所在的页面是否指向我自己 */
	public boolean me;
	/** 地区 */
	public String regionCode;
	public List<String> region;
	/** 星座 */
	public String zodiac;
	/** 摘要简介 */
	public String summary;
	/** 感兴趣的类型 */
	public List<String> selfPurposes;
	/** 我自己的类型 */
	public String selfTag;
	public String avatarImageId;
	/** 资料不完整 强制跳 */
	public boolean profileIncomplete;
	/** 他喜欢多少人 */
	public int alohaCount;
	/** 他被多少人喜欢 */
	public int alohaGetCount;
	/** 是否喜欢过他 */
	public boolean aloha;
	/** 匹配 */
	public boolean match;
	public int postCount;
	// 是否屏蔽
	public boolean block;
	public boolean hasPrivacy;

	public ImageDTO avatarImage;
}
