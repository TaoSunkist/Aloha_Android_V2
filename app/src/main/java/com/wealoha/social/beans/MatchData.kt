package com.wealoha.social.beans

import com.mooveit.library.Fakeit
import com.wealoha.social.beans.imagemap.HasImageMap
import java.io.Serializable

/**
 * Match调用结果，下一批待匹配的用户或者错误
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-29 下午4:44:17
 */
class MatchData(
    var list: List<User>? = null,

    /** 还需要多少秒才能刷下一批  */
    var quotaResetSeconds: Int = 0,

    /** 每次刷一批的时间段长度  */
    var quotaDurationSeconds: Int = 0,

    /** 可用的重置配额次数  */
    var quotaReset: Int = 0,
    override var imageMap: Map<String, Image> = hashMapOf(),
    var recommendSourceMap: Map<String, String> = hashMapOf()
) : ResultData(), Serializable, HasImageMap {

    companion object {
        fun fake(): MatchData {

            val userList = arrayListOf<User>()
            val imageMap = hashMapOf<String, Image>()
            val recommendSourceMap = hashMapOf<String, String>()

            (0..10).forEach {
                val user = User.fake()
                userList.add(user)
                imageMap[user.avatarImageId] = Image.fake()
                recommendSourceMap[user.id] = Fakeit.book().title()
            }

            return MatchData(
                quotaDurationSeconds = listOf(30, 45, 60).random(),
                quotaResetSeconds = listOf(5, 10, 15).random(),
                quotaReset = (1..3).random(),
                list = userList,
                imageMap = imageMap,
                recommendSourceMap = recommendSourceMap
            )
        }

        private const val serialVersionUID = -7473243341042126058L

        @kotlin.jvm.JvmField
        val TAG = MatchData::class.java.simpleName
    }
}