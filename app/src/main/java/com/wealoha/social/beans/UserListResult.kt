package com.wealoha.social.beans

import com.wealoha.social.beans.imagemap.HasImageMap
import java.io.Serializable

data class UserListResult(
    var list: List<User>? = null,
    override var imageMap: Map<String, Image>? = null,
    var nextCursorId: String? = null,
    var alohaGetLocked: Boolean = false,
    var alohaGetUnlockCount: Int = 0

) : ResultData(), Serializable, HasImageMap {

    companion object {
        /**
         * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
         */
        private const val serialVersionUID = 8456520717379285862L
        val TAG = UserListResult::class.java.simpleName

        fun fake(): UserListResult {
            val list = arrayListOf<User>()
            val imageMap = hashMapOf<String, Image>()
            (0..10).map {
                val user = User.fake()
                list.add(user)
                imageMap.put(user.id, Image.fake())
                user
            }.toList()
            return UserListResult(
                list = list,
                imageMap = imageMap,
                nextCursorId = Direct.Early.value,
                alohaGetLocked = false,
                alohaGetUnlockCount = 0
            )
        }
    }
}