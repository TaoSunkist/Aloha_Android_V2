package com.wealoha.social.beans

import java.util.*

class TopicPost {
    /**
     * 适应gridview 样式实图，三个post一行
     */
    var postsItem: List<Post>

    /** List<TopicPost> 是否满足一行三个 </TopicPost> */
    var isItemFull = true

    /** List<TopicPost> 有几个空位 </TopicPost> */
    var isVacancyCount = 0
        private set
    var isTitleItem = false
    var titleId = 0
    var itemType = 0
    var hashTag: HashTag? = null

    fun setVacancyCount() {
        isVacancyCount++
    }

    companion object {
        const val RELODE_TYPE = 0x0001
        const val NOMORE_TYPE = 0x0002
        const val TITLE_TYPE = 0x0003
        const val NORMAL_TYPE = 0x0004
    }

    init {
        postsItem = ArrayList()
    }
}