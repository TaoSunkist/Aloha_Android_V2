package com.wealoha.social.api.locationservice.bean;


public class LocationServiceStatus {
	public boolean LocationServiceEnable; 
	public int errorCode;
	public LocationServiceStatus(boolean enable, int errorCode){
		LocationServiceEnable = enable;
		this.errorCode = errorCode;
	}
}
