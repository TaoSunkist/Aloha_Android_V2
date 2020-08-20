package com.wealoha.social.beans

import java.io.Serializable

/***
 * @author superman
 */
class CommonVideo(
    private val id: String?,
    private val width: Int,
    private val height: Int,
    private val url: String?
) : Serializable {
    fun getId(): String? {
        return id
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun getUrl(): String? {
        return url
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -8304279904338894714L

        /***
         * TODO
         *
         * @param videoCommonDTO
         * @return 参数为空，则返回值为空
         */
        @kotlin.jvm.JvmStatic
        fun fromDTO(videoCommonDTO: VideoCommonDTO?): CommonVideo? {
            return if (videoCommonDTO == null) {
                null
            } else CommonVideo(
                videoCommonDTO.videoId,
                videoCommonDTO.width,
                videoCommonDTO.height,
                videoCommonDTO.url
            )
        }

        fun getSerialversionuid(): Long {
            return serialVersionUID
        }
    }

}