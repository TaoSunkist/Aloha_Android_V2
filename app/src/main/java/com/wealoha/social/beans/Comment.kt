package com.wealoha.social.beans

class Comment {
    @kotlin.jvm.JvmField
    var createTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var userId: String? = null
    @kotlin.jvm.JvmField
    var mine = false
    @kotlin.jvm.JvmField
    var id: String? = null
    var type: String? = null
    @kotlin.jvm.JvmField
    var comment: String? = null

    /** 回复某人  */
    @kotlin.jvm.JvmField
    var replyUserId: String? = null
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Comment
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }
}