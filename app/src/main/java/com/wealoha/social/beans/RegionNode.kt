package com.wealoha.social.beans

import java.io.Serializable

/**
 * 地区
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-28 下午2:43:28
 */
data class RegionNode(
    var name: String,
    /** 缩写，如果有优先显示缩写  */
    var abbr: String,
    var regions: Map<String, RegionNode>
) : Serializable {

    companion object {
        private const val serialVersionUID = -5175776288479428533L

        @kotlin.jvm.JvmField
        val TAG = RegionNode::class.java.name
    }

}