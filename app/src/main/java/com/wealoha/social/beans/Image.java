package com.wealoha.social.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

	public String id;
	public String type;
	public int width;
	public int height;
	public String url;
	public String urlPatternWidth;
	public String urlPatternWidthHeight;

	public String path;
	public String mimeType;

	public Image() {
	}

	public Image(Parcel source) {
		id = source.readString();
		type = source.readString();
		width = source.readInt();
		height = source.readInt();
		url = source.readString();
		urlPatternWidth = source.readString();
		urlPatternWidthHeight = source.readString();
		path = source.readString();
		mimeType = source.readString();
	}

	public static final Parcelable.Creator<Image> CREATOR = new Creator<Image>() {

		@Override
		public Image createFromParcel(Parcel source) {
			return new Image(source);
		}

		@Override
		public Image[] newArray(int size) {
			return new Image[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(type);
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeString(url);
		dest.writeString(urlPatternWidth);
		dest.writeString(urlPatternWidthHeight);
		dest.writeString(path);
		dest.writeString(mimeType);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlPatternWidth() {
		return urlPatternWidth;
	}

	public void setUrlPatternWidth(String urlPatternWidth) {
		this.urlPatternWidth = urlPatternWidth;
	}

	public String getUrlPatternWidthHeight() {
		return urlPatternWidthHeight;
	}

	public void setUrlPatternWidthHeight(String urlPatternWidthHeight) {
		this.urlPatternWidthHeight = urlPatternWidthHeight;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}