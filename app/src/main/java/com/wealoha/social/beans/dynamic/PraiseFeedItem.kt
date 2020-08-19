package com.wealoha.social.beans.dynamic

class PraiseFeedItem : BaseDynamicItem() {
    /**
     * 头像的数组.
     */
    var mCircleImageViews: IntArray = intArrayOf()

    /**
     * 被赞的Feed图片.
     */
    var mFeedImg: String? = null
}