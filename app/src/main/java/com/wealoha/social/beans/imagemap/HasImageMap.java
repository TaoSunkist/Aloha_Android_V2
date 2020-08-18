package com.wealoha.social.beans.imagemap;

import java.util.Map;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ResultData;

/**
 * 表示当前返回的接口有Image字典<br/>
 * {@link ResultData} 的子类继承
 * 
 * @author hongwei
 * @see
 * @since
 * @date 2015-1-13 下午2:38:03
 */
public interface HasImageMap {

	public Map<String, Image> getImageMap();
}
