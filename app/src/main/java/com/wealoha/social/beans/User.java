package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5164011748109511531L;
	public static final String TAG = User.class.getSimpleName();
	public String id;
	public String type;
	public String name;
	public String birthday; // yyyy-MM-dd
	public String age; // TODO 改成int！！！
	public String height; // 1+
	public String weight; // 1+
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
	/** 头像 */
	public Image avatarImage;
	public String avatarImageId;
	// public int distance;
	public long createTimeMillis;
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
	@Deprecated
	public String t;
	public String phoneNum;
	public boolean block;
	public String accessToken;
	public boolean isUpdate;

	/** 是否显示首次aloha time over后的提示 */
	public boolean isShowAlohaTimeDialog = false;
	/** 是否显示首次feed的引导 */
	public boolean isShowFeedDialog = false;

	public boolean hasPrivacy;

	public User() {
	}

	public User(Parcel source) {
		id = source.readString();
		type = source.readString();
		name = source.readString();
		birthday = source.readString();
		age = source.readString();
		height = source.readString();
		weight = source.readString();
		me = source.readByte() != 0;
		regionCode = source.readString();
		if (region == null) {
			region = new ArrayList<String>();
		}
		source.readStringList(region);
		zodiac = source.readString();
		summary = source.readString();

		if (selfPurposes == null) {
			selfPurposes = new ArrayList<String>();
		}
		source.readStringList(selfPurposes);
		selfTag = source.readString();
		avatarImage = (Image) source.readParcelable(Image.class.getClassLoader());
		avatarImageId = source.readString();
		createTimeMillis = source.readLong();
		profileIncomplete = source.readByte() != 0;
		alohaCount = source.readInt();
		alohaGetCount = source.readInt();
		aloha = source.readByte() != 0;
		match = source.readByte() != 0;
		postCount = source.readInt();
		t = source.readString();
		phoneNum = source.readString();
		block = source.readByte() != 0;
		accessToken = source.readString();
		isUpdate = source.readByte() != 0;
		isShowAlohaTimeDialog = source.readByte() != 0;
		isShowFeedDialog = source.readByte() != 0;
		hasPrivacy = source.readByte() != 0;

	}

	public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(name);
		dest.writeString(birthday);
		dest.writeString(age);
		dest.writeString(height);
		dest.writeString(weight);
		dest.writeByte((byte) (me ? 1 : 0));
		dest.writeString(regionCode);
		dest.writeStringList(region);
		dest.writeString(zodiac);
		dest.writeString(summary);
		dest.writeStringList(selfPurposes);
		dest.writeString(selfTag);
		dest.writeParcelable(avatarImage, flags);
		dest.writeString(avatarImageId);
		dest.writeLong(createTimeMillis);
		dest.writeByte((byte) (profileIncomplete ? 1 : 0));
		dest.writeInt(alohaCount);
		dest.writeInt(alohaGetCount);
		dest.writeByte((byte) (aloha ? 1 : 0));
		dest.writeByte((byte) (match ? 1 : 0));
		dest.writeInt(postCount);
		dest.writeString(t);
		dest.writeString(phoneNum);
		dest.writeByte((byte) (block ? 1 : 0));
		dest.writeString(accessToken);
		dest.writeByte((byte) (isUpdate ? 1 : 0));
		dest.writeByte((byte) (isShowAlohaTimeDialog ? 1 : 0));
		dest.writeByte((byte) (isShowFeedDialog ? 1 : 0));
		dest.writeByte((byte) (hasPrivacy ? 1 : 0));
	}

}
