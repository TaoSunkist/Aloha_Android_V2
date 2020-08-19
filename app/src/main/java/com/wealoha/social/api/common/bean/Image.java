package com.wealoha.social.api.common.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;

/**
 * @author javamonk
 * @createTime 2015年2月25日 上午11:31:55
 */
public class Image implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8304279904338894714L;
    private final String id;
    private final int width;
    private final int height;
    private final String urlPatternWidth;
    private final String urlPatternWidthHeight;

    public Image(String imageId, int width, int height, String urlPatternWidth, String urlPatternWidthHeight) {
        super();
        this.id = imageId;
        this.width = width;
        this.height = height;
        this.urlPatternWidth = urlPatternWidth;
        this.urlPatternWidthHeight = urlPatternWidthHeight;
    }

    public static Image fromDTO(ImageDTO imageDTO) {
        if (imageDTO == null) {
            return null;
        } else {
            return new Image(imageDTO.imageId, imageDTO.width, imageDTO.height, //
                    imageDTO.urlPatternWidth, imageDTO.urlPatternWidthHeight);
        }
    }

    public static Image fromOld(com.wealoha.social.beans.Image oldImg) {
        if (oldImg == null) {
            return null;
        } else {
            return new Image(oldImg.getId(), oldImg.getWidth(), oldImg.getHeight(), //
                    oldImg.getUrlPatternWidth(), oldImg.getUrlPatternWidthHeight());
        }
    }

    // public static com.wealoha.social.beans.Image toOld(Image img) {
    // com.wealoha.social.beans.Image oldimg = new com.wealoha.social.beans.Image();
    // oldimg.id = img.getImageId();
    // oldimg.height = img.getHeight();
    // oldimg.width = img.getWidth();
    // oldimg.urlPatternWidth = img.get
    // }

    public String getImageId() {
        return id;
    }

    /**
     * 原图宽度
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 原图高度
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取方图地址
     *
     * @param width
     * @return
     */
    public String getUrlSquare(int width) {
        if (StringUtils.isNotBlank(urlPatternWidth)) {
            return replacePattern(urlPatternWidth, width + "");
        }
        return ImageUtil.getImageUrl(id, width, CropMode.ScaleCenterCrop);
    }

    /**
     * 获取等比例图地址，请参考 {@link #getWidth()}, {@link #getHeight()} 计算
     *
     * @param width
     * @param height
     * @return
     */
    public String getUrl(int width, int height) {
        if (StringUtils.isNotBlank(urlPatternWidthHeight)) {
            return replacePattern(urlPatternWidthHeight, width + "", height + "");
        }
        return ImageUtil.getImageUrl(id, width, null);
    }

    private String replacePattern(String target, Object... args) {
        target = StringUtils.replace(target, "%@", "%s");
        return String.format(target, args);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
