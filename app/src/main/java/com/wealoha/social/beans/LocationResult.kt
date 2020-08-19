package com.wealoha.social.beans

class LocationResult : ResultData() {
    @kotlin.jvm.JvmField
    var nextCursorId: String? = null
    var source: String? = null
    @kotlin.jvm.JvmField
    var list: List<Location>? = null
}