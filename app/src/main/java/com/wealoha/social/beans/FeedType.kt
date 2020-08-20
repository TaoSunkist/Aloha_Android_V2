package com.wealoha.social.beans

import com.wealoha.social.widget.MultiListViewType
import java.util.*

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:45:30
 */
enum class FeedType(//
    val value: String, private val mViewType: Int
) : MultiListViewType {
    VideoPost("VideoPost", 0),  //
    ImagePost("ImagePost", 1);

    companion object {
        private var valuesMap: MutableMap<String, FeedType> = HashMap()

        @kotlin.jvm.JvmStatic
        fun fromValue(value: String?): FeedType? {
            return valuesMap!![value]
        }

        init {
            for (t in values()) {
                valuesMap[t.value] = t
            }
        }
    }

    override fun getViewType(): Int {
        return mViewType
    }

}