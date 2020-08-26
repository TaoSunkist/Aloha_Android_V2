package com.wealoha.social.beans

import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User

data class FindYouResult(
    var highlightMap: Map<String, String>? = null,
    var list: List<User>? = null,
    var user: User? = null,
    var keyword: String? = null
) : ResultData() {

}