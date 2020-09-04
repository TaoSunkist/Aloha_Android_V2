package com.wealoha.social.beans

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:39:02
 */
interface Notify2 {
    val type: Notify2Type
    val isUnread: Boolean
    val updateTimeMillis: Long

    fun changeReadState(isread: Boolean)

    companion object {
        /**
         * feed被喜欢的通知.
         *
         */
        @Deprecated("")
        const val POST_LIKE_VIEW_TYPE = 0

        /**
         * 人气的通知
         *
         */
        @Deprecated("")
        const val NEW_ALOHA_VIEW_TYPE = 1

        /**
         * Feed评论的通知
         *
         */
        @Deprecated("")
        const val POST_COMMENT_TAG_TYPE = 2

        /**
         * 圈人的通知
         *
         */
        @Deprecated("")
        const val POST_TAG_VIEW_TYPE = 3
    }
}