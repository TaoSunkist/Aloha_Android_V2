package com.wealoha.social.beans.user

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User
import com.wealoha.social.beans.imagemap.HasImageMap

/**
 * 查看/修改用户Profile以后的结果
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-21 上午11:26:28
 */
class ProfileData : ResultData(), HasImageMap {
    @kotlin.jvm.JvmField
    var user: User? = null
    override var imageMap: Map<String, Image>? = null
}