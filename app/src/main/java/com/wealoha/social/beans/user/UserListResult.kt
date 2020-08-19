package com.wealoha.social.beans.user

import com.wealoha.social.beans.Image
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User
import com.wealoha.social.beans.imagemap.HasImageMap
import java.io.Serializable

class UserListResult : ResultData(), Serializable, HasImageMap {
    @kotlin.jvm.JvmField
    var list: List<User>? = null
    override var imageMap: Map<String, Image>? = null
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
    var alohaGetLocked = false
    @kotlin.jvm.JvmField
    var alohaGetUnlockCount = 0

    companion object {
        /**
         * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
         */
        private const val serialVersionUID = 8456520717379285862L
        val TAG = UserListResult::class.java.simpleName
    }
}