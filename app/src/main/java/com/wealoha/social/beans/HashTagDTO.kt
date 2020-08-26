package com.wealoha.social.beans

import com.mooveit.library.Fakeit
import com.wealoha.social.utils.Debug

data class HashTagDTO(// itemId: "",
    // name: "name",
    // bgImage:"背景图片",
    // summary: "描述",
    // url: "",
    // postCount: "post数量",
    // type: "HashTag"
    var itemId: String,
    var name: String,
    var backgroundImage: ImageCommonDto? = null,
    var summary: String,
    var postCount: Int = 0,
    var url: String,
    var type: String? = null,
    var userCount: Int = 0
) {
    companion object{
        fun fake(): HashTagDTO {
            return HashTagDTO(
                itemId = System.currentTimeMillis().toString(),
                name = Fakeit.book().author(),
                backgroundImage = ImageCommonDto.fake(),
                summary = Fakeit.ancient().titan(),
                postCount = (0..20) .random(),
                url = Debug.images.random(),
                type = "",
                userCount = (0..20).random()
            )
        }
    }

}