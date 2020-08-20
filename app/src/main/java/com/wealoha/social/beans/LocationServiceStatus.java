package com.wealoha.social.beans;


public class LocationServiceStatus {
	public boolean LocationServiceEnable; 
	public int errorCode;
	public LocationServiceStatus(boolean enable, int errorCode){
		LocationServiceEnable = enable;
		this.errorCode = errorCode;
	}
}
