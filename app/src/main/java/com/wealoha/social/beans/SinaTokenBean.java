package com.wealoha.social.beans;

import com.google.gson.annotations.SerializedName;

public class SinaTokenBean extends ResultData {
	@SerializedName("access_token")
	public String accessToken;
	public String uid;
	public String name;
	@SerializedName("expires_in")
	public String expiresIn;
}
