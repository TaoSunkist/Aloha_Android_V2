package com.wealoha.social.beans.imagemap

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData

/**
 * 表示当前返回的接口有Image字典<br></br>
 * [ResultData] 的子类继承
 *
 * @author hongwei
 * @see
 * @since
 * @date 2015-1-13 下午2:38:03
 */
interface HasImageMap {
    val imageMap: Map<String, Image>?
}