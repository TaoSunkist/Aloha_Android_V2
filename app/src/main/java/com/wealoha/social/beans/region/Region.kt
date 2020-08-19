package com.wealoha.social.beans.region

import java.io.Serializable

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月9日
 */
class Region : Serializable {
    var code: String? = null
    var name: String? = null

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 4023665817362808580L
    }
}