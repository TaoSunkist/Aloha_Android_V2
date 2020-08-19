package com.wealoha.social.beans

import java.io.Serializable
import java.util.*

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月9日
 */
class RegionResult : ResultData(), Serializable {
    var filterEnable = false
    var filterAgeRangeStart = 0
    var filterAgeRangeEnd = 0
    var filterRegion: String? = null
    var regions: ArrayList<Regions>? = null
    var selectedRegion: ArrayList<String>? = null

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1853858837485072423L
    }
}