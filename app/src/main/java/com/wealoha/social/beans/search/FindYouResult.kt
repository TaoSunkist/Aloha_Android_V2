package com.wealoha.social.beans.search

import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.User

class FindYouResult : ResultData() {
    @kotlin.jvm.JvmField
    var highlightMap: Map<String, String>? = null
    @kotlin.jvm.JvmField
    var list: List<User>? = null
    @kotlin.jvm.JvmField
    var user: User? = null
    @kotlin.jvm.JvmField
    var keyword: String? = null
}