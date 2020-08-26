package com.wealoha.social.beans

import com.wealoha.social.utils.Debug
import java.lang.System.*

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:31:07
 */
data class VideoCommonDTO(
    var videoId: String,
    var width: Int = 0,
    var heigt: Int = 0,
    var url: String? = null
) {
    companion object {
        fun fake(): VideoCommonDTO {
            return VideoCommonDTO(
                videoId = currentTimeMillis().toString(),
                width = listOf(200, 400, 800).random(),
                heigt = listOf(200, 400, 800).random(),
                url = Debug.images.random()
            )
        }

        fun fakeForMap(): HashMap<String, VideoCommonDTO> {
            val map = hashMapOf<String, VideoCommonDTO>()
            (0..20).forEach { _ ->
                map[currentTimeMillis().toString()] = VideoCommonDTO.fake()
            }
            return map
        }
    }
}