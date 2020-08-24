package com.wealoha.social.beans

import android.os.Parcelable
import com.wealoha.social.utils.Debug
import com.wealoha.social.utils.Dimens
import com.wealoha.social.utils.ImageUtil
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils

@Parcelize
class Image(
    var id: String,
    var type: String? = null,
    var width: Int = 0,
    var height: Int = 0,
    var url: String,
    var urlPatternWidth: String? = null,
    var urlPatternWidthHeight: String? = null,
    var path: String? = null,
    var mimeType: String? = null
) : Parcelable {

    companion object {
        fun fake(): Image {
            return Image(
                id = System.currentTimeMillis().toString(),
                type = System.currentTimeMillis().toString(),
                url = Debug.images.random(),
                width = Dimens.purchaseableItemWidth,
                height = Dimens.purchaseableItemHeight
            )
        }

        fun init(imageCommonDto: ImageCommonDto): Image {
            return Image(
                id = imageCommonDto.imageId,
                type = imageCommonDto.type,
                width = imageCommonDto.width,
                height = imageCommonDto.height,
                url = Debug.images.random()
            )
        }

        fun init(commonImage: CommonImage): Image {
            return Image(
                id = commonImage.id,
                type = "",
                width = commonImage.width,
                height = commonImage.height,
                url = Debug.images.random()
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
        } else ImageUtil.getImageUrl(id, width, ImageUtil.CropMode.ScaleCenterCrop)
    }

    private fun replacePattern(target: String?, vararg args: Any): String {
        var target = target
        target = StringUtils.replace(target, "%@", "%s")
        return String.format(target, *args)
    }
}