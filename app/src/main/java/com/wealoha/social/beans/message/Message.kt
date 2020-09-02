package com.wealoha.social.beans.message

/**
 * Created by walker on 14-4-15.
 */
open class Message(
    open var id: String,
    open var isLocal: Boolean = false,
    open var sending: Boolean,
    open var sendFail: Boolean,
    open var mine: Boolean,
    open var state: String,
    open var createTimeMillis: Long,
    open var showTimestamp: Boolean
) {
}