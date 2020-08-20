package com.wealoha.social.beans

import com.wealoha.social.widget.MultiListViewType
import java.util.*

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:45:30
 */
enum class Notify2Type(//
    private val value: String, private val mViewType: Int
) :
    MultiListViewType {
    PostLike("PostLike", 0),  //
    NewAloha("NewAloha", 1),  //
    PostComment("PostComment", 2),  //
    PostTag("PostTag", 3),  //
    PostCommentReplyOnMyPost("PostCommentReplyOnMyPost", 4),  //
    PostCommentReplyOnOthersPost("PostCommentReplyOnOthersPost", 5);

    companion object {
        private var valuesMap: MutableMap<String, Notify2Type> = HashMap()

        @kotlin.jvm.JvmStatic
        fun fromValue(value: String?): Notify2Type? {
            return valuesMap!![value]
        }

        init {
            for (t in values()) {
                valuesMap[t.getValue()] = t
            }
        }
    }

    override fun getViewType(): Int {
        return mViewType
    }

    fun getValue(): String {
        return value
    }

}