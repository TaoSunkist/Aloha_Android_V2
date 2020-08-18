package com.wealoha.social.api.user

import com.wealoha.social.api.common.dto.ImageDTO
import com.wealoha.social.api.user.dto.UserDTO
import com.wealoha.social.beans.ResultData

class Profile2Data : ResultData() {
    var user: UserDTO? = null
    var imageMap: Map<String, ImageDTO>? = null
    var friend = false
    var liked = false
}