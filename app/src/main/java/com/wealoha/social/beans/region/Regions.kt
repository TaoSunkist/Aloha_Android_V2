package com.wealoha.social.beans.region

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
class Regions : Serializable {
    @kotlin.jvm.JvmField
    var code: String? = null
    @kotlin.jvm.JvmField
    var name: String? = null
    @kotlin.jvm.JvmField
    var regions: ArrayList<Regions>? = null

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -5690191620144863785L
    }
}