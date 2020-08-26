package com.wealoha.social.beans

data class LocationResult(
    var nextCursorId: String? = null,
    var source: String? = null,
    var list: List<Location>? = null
) : ResultData() {

}