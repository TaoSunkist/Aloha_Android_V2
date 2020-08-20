package com.wealoha.social.beans

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.io.Serializable

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:44:54
 */
abstract class AbsNotify2 // FIXME api应当返回notifyId，暂时用这个开发调试
    (
    override val type: Notify2Type,
    // 未读，不是final，用来重新渲染视图
    override var isUnread: Boolean,
    // 用来做对象相等比较
    val notifyId: String?,
    override val updateTimeMillis: Long
) : Notify2, Serializable {

    override fun changeReadState(isread: Boolean) {
        isUnread = isread
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (notifyId?.hashCode() ?: 0)
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as AbsNotify2
        val notifyId = notifyId
        if (notifyId == null) {
            if (other.notifyId != null) return false
        } else if (notifyId != other.notifyId) return false
        return true
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    companion object {
        private const val serialVersionUID = -5031739406933536965L
    }

}