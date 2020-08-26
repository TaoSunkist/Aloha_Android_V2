package com.wealoha.social.beans

import com.wealoha.social.utils.ImageUtil
import com.wealoha.social.utils.ImageUtil.CropMode
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.io.Serializable

/**
 * @author javamonk
 * @createTime 2015年2月25日 上午11:31:55
 */
class CommonImage(
    // public static com.wealoha.social.beans.Image toOld(Image img) {
    // com.wealoha.social.beans.Image oldimg = new com.wealoha.social.beans.Image();
    // oldimg.id = img.getImageId();
    // oldimg.height = img.getHeight();
    // oldimg.width = img.getWidth();
    // oldimg.urlPatternWidth = img.get
    // }
    val id: String,
    /**
     * 原图宽度
     *
     * @return
     */
    val width: Int,
    /**
     * 原图高度
     *
     * @return
     */
    val height: Int,
    private val urlPatternWidth: String?,
    private val urlPatternWidthHeight: String?
) : Serializable {

    /**
     * 获取方图地址
     *
     * @param width
     * @return
     */
    fun getUrlSquare(width: Int): String {
        return if (StringUtils.isNotBlank(urlPatternWidth)) {
            replacePattern(urlPatternWidth, width.toString() + "")
        } else ImageUtil.getImageUrl(id, width, CropMode.ScaleCenterCrop)
    }

    /**
     * 获取等比例图地址，请参考 [.getWidth], [.getHeight] 计算
     *
     * @param width
     * @param height
     * @return
     */
    fun getUrl(width: Int, height: Int): String {
        return if (StringUtils.isNotBlank(urlPatternWidthHeight)) {
            replacePattern(
                urlPatternWidthHeight,
                width.toString() + "",
                height.toString() + ""
            )
        } else ImageUtil.getImageUrl(id, width, null)
    }

    private fun replacePattern(target: String?, vararg args: Any): String {
        var target = target
        target = StringUtils.replace(target, "%@", "%s")
        return String.format(target, *args)
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -8304279904338894714L

        @kotlin.jvm.JvmStatic
        fun fromDTO(imageCommonDto: ImageCommonDto): CommonImage {
            return CommonImage(
                imageCommonDto.imageId, imageCommonDto.width, imageCommonDto.height,  //
                imageCommonDto.urlPatternWidth, imageCommonDto.urlPatternWidthHeight
            )
        }

        fun fromOld(oldImg: Image): CommonImage {
            return CommonImage(
                oldImg.id, oldImg.width, oldImg.height,  //
                oldImg.urlPatternWidth, oldImg.urlPatternWidthHeight
            )
        }
    }

}