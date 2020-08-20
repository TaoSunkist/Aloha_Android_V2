package com.wealoha.social.beans

import java.io.Serializable
import java.util.*

class MatchSettingData : ResultData(), Serializable {
    @JvmField
    var filterEnable = false
    @JvmField
    var filterRegion: String? = null
    @JvmField
    var filterAgeRangeStart: Int? = null
    @JvmField
    var filterAgeRangeEnd: Int? = null
    @JvmField
    var regions: ArrayList<Regions>? = null
    @JvmField
    var selectedRegion: List<String>? = null

    companion object {
        private const val serialVersionUID = -6856543043928783808L
        @JvmField
        val TAG = MatchSettingData::class.java.name
    }
}