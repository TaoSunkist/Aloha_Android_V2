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
data class ImageCommonDto(
    var imageId: String,
    var width: Int,
    var height: Int,
    var urlPatternWidth: String? = null,
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

        fun fakeForMap(): HashMap<String, ImageCommonDto> {
            val map = hashMapOf<String, ImageCommonDto>()
            (0..20).forEach { index ->
                map[index.toString()] = fake()
            }
            return map
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