package com.wealoha.social.endpoint;

import java.util.List;

import retrofit.Endpoint;

import com.wealoha.social.commons.GlobalConstants;

/**
 * API入口提供者
 * 
 * http://stackoverflow.com/questions/23279006/dynamic-paths-in-retrofit
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2015-1-13 上午10:52:03
 */
public class ApiEndpoint implements Endpoint {

	private String endpoint = GlobalConstants.ServerUrl.SERVER_URL;

	private List<String> urlFail;
	private List<String> urlOk;

	/**
	 * 修改地址
	 * 
	 * @param endpoint
	 *            http://xxxx.xx.xx or http://ip
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public List<String> getUrlFail() {
		return urlFail;
	}

	public void setUrlFail(List<String> urlFail) {
		this.urlFail = urlFail;
	}

	public List<String> getUrlOk() {
		return urlOk;
	}

	public void setUrlOk(List<String> urlOk) {
		this.urlOk = urlOk;
	}

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public String getUrl() {
		return endpoint;
	}
}
