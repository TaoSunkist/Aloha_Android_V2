package com.wealoha.social.beans;

import java.io.Serializable;

/***
 * @author superman
 *
 */
public class CommonVideo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8304279904338894714L;
	private final String id;
	private final int width;
	private final int height;
	private final String url;

	public CommonVideo(String videoId, int width, int height, String url) {
		super();
		this.id = videoId;
		this.width = width;
		this.height = height;
		this.url = url;
	}

	/***
	 * TODO
	 * 
	 * @param videoCommonDTO
	 * @return 参数为空，则返回值为空
	 */
	public static CommonVideo fromDTO(VideoCommonDTO videoCommonDTO) {
		if (videoCommonDTO == null) {
			return null;
		}
		return new CommonVideo(videoCommonDTO.videoId, videoCommonDTO.width, videoCommonDTO.height, videoCommonDTO.url);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getUrl() {
		return url;
	}

}
