package com.wealoha.social.beans

import com.wealoha.social.utils.Debug
import com.wealoha.social.utils.Dimens
import com.wealoha.social.utils.ImageUtil
import org.apache.commons.lang3.StringUtils

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

    /**
     * 获取方图地址
     *
     * @param width
     * @return
     */
    fun getUrlSquare(width: Int): String {
        return if (StringUtils.isNotBlank(urlPatternWidth)) {
            replacePattern(urlPatternWidth, width.toString() + "")
        } else ImageUtil.getImageUrl(imageId, width, ImageUtil.CropMode.ScaleCenterCrop)
    }

    private fun replacePattern(target: String?, vararg args: Any): String {
        var target = target
        target = StringUtils.replace(target, "%@", "%s")
        return String.format(target, *args)
    }
}