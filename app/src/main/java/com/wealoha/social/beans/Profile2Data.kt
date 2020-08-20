package com.wealoha.social.beans

class Profile2Data : ResultData() {
    var user: UserDTO? = null
    var imageMap: Map<String, ImageCommonDto>? = null
    var friend = false
    var liked = false
}