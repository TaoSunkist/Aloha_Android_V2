package com.wealoha.social.model.feeds;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月21日
 */
public class AdvertisementModel implements com.wealoha.social.model.feeds.IAdvertisementModel {
	public String imgUrl = "http://7u2nrz.com1.z0.glb.clouddn.com/ad_test.jpg";

	@Override
	public String getAdvertisementImgUrl() {
		return imgUrl;
	}
}
