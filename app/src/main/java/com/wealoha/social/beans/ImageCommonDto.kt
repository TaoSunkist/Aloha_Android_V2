package com.wealoha.social.beans

import com.wealoha.social.utils.Dimens

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:31:07
 */
class ImageCommonDto(
    @JvmField
    var imageId: String,
    @JvmField
    var width: Int,
    @JvmField
    var height: Int,
    @JvmField
    var urlPatternWidth: String? = null,
    @JvmField
    var urlPatternWidthHeight: String? = null,
    var type: String? = null
) {
    companion object {
        fun fake(): ImageCommonDto {
            return ImageCommonDto(
                imageId = System.currentTimeMillis().toString(),
                width = Dimens.purchaseableItemWidth,
                height = Dimens.purchaseableItemHeight
            )
        }
    }
}