package com.wealoha.social.module;

import java.util.ArrayList;
import java.util.List;

import com.wealoha.social.beans.User;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 人气的notify
 * @copyright wealoha.com
 * @Date:2015-2-13
 */
public class AlohaItem extends NotifyItem {
	/**
	 * 喜欢你的User的集合
	 */
	public final List<User> mAlohaUsers = new ArrayList<User>();
	/**
	 * aloha你的人数
	 */
	public int count;
	/**
	 * View的行数
	 */
	public int lineCount;
	/**
	 * 每行头像的个数
	 */
	public int iconCount;

	public void clacViewParameter() {

	}
}
