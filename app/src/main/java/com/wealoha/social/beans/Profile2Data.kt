package com.wealoha.social.beans

import com.wealoha.social.api.common.dto.ImageDTO

class Profile2Data : ResultData() {
    var user: UserDTO? = null
    var imageMap: Map<String, ImageDTO>? = null
    var friend = false
    var liked = false
}