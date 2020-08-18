package com.wealoha.social.beans.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.wealoha.social.beans.User;

public class UserTags implements Parcelable {

	public final static String TAG = UserTags.class.getSimpleName();

	public Float tagAnchorX;
	public Float tagAnchorY;
	public Float tagCenterX;
	public Float tagCenterY;
	public String tagUserId;
	public User tagUser;
	// 以下属性是服务器没有的字段
	public String tagUserName;
	public boolean tagMe;

	public UserTags() {
	}

	public UserTags(Parcel source) {
		setTagAnchorX((Float) source.readValue(Float.class.getClassLoader()));
		setTagAnchorY((Float) source.readValue(Float.class.getClassLoader()));
		setTagCenterX((Float) source.readValue(Float.class.getClassLoader()));
		setTagCenterY((Float) source.readValue(Float.class.getClassLoader()));
		setTagUserId(source.readString());

		setTagUser((User) source.readParcelable(User.class.getClassLoader()));

		setUsername(source.readString());
		setTagMe(source.readByte() != 0);
	}

	public static final Parcelable.Creator<UserTags> CREATOR = new Creator<UserTags>() {

		@Override
		public UserTags createFromParcel(Parcel source) {
			return new UserTags(source);
		}

		@Override
		public UserTags[] newArray(int size) {
			return new UserTags[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(tagAnchorX);
		dest.writeValue(tagAnchorY);
		dest.writeValue(tagCenterX);
		dest.writeValue(tagCenterY);
		dest.writeString(tagUserId);

		dest.writeParcelable(tagUser, flags);

		dest.writeString(tagUserName);
		dest.writeByte((byte) (tagMe ? 1 : 0));
	}

	public Float getTagAnchorX() {
		return tagAnchorX;
	}

	public void setTagAnchorX(Float tagAnchorX) {
		this.tagAnchorX = tagAnchorX;
	}

	public Float getTagAnchorY() {
		return tagAnchorY;
	}

	public void setTagAnchorY(Float tagAnchorY) {
		this.tagAnchorY = tagAnchorY;
	}

	public Float getTagCenterX() {
		return tagCenterX;
	}

	public void setTagCenterX(Float tagCenterX) {
		this.tagCenterX = tagCenterX;
	}

	public Float getTagCenterY() {
		return tagCenterY;
	}

	public void setTagCenterY(Float tagCenterY) {
		this.tagCenterY = tagCenterY;
	}

	public String getTagUserId() {
		return tagUserId;
	}

	public void setTagUserId(String tagUserId) {
		this.tagUserId = tagUserId;
	}

	public boolean getTagMe() {
		return tagMe;
	}

	public void setTagMe(boolean tagMe) {
		this.tagMe = tagMe;
	}

	public String getUsername() {
		return tagUserName;
	}

	public void setUsername(String username) {
		this.tagUserName = username;
	}

	public User getTagUser() {
		return tagUser;
	}

	public void setTagUser(User tagUser) {
		this.tagUser = tagUser;
	}

}
