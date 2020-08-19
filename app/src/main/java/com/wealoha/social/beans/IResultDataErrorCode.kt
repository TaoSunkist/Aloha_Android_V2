package com.wealoha.social.beans

interface IResultDataErrorCode {
    companion object {
        /** 手機號格式錯誤  */
        const val MOBILE_ERROR = 200520

        /** 用戶已註冊  */
        const val REGISTERED_ERROR = 200516
        const val ERROR_USER_NOT_FOUND = 200502

        /** 用户名不可用  */
        const val ERROR_USERNAME_USED = 200503
        const val ERROR_EMAIL_USED = 200504
        const val ERROR_IMAGE_UPLOAD_FAIL = 200505
        const val ERROR_AUDIO_UPLOAD_FAIL = 200506
        const val ERROR_EMOTION_NOT_FOUND = 200507
        const val ERROR_LICENSE_INVALID = 200508
        const val ERROR_INVALID_PASSWORD = 200509
        const val ERROR_INVALID_EMAIL = 200510
        const val ERROR_INVALID_PASSWORD_RESET_CODE = 200511
        const val ERROR_BLOCK_BY_YOUR_SELF = 200513
        const val ERROR_BLOCK_BY_OTHER = 200514
        const val ERROR_INBOX_FOR_FRIENDS_ONLY = 200515
        const val ERROR_MOBILE_NUMBER_REGISTERED = 200516
        const val ERROR_INVALID_MOBILE_VERIFY_CODE = 200517

        /** 当前时段没有更多匹配了  */
        const val ERROR_MATCH_NO_MORE_TODAY = 200518
        const val ERROR_MATCH_NO_MORE_DATA = 200519
        const val ERROR_INVALID_MOBILE_NUMBER = 200520

        /** 简介有关键词  */
        const val ERROR_INVALID_SUMMARY = 200522
        const val ERROR_MATCH_NOPE = 200523

        /** accessToken无效  */
        const val ERROR_INVALID_ACCESS_TOKEN = 200524

        /** 访问微博失败，稍候再试  */
        const val ERROR_CONNECT_REMOTE_FAIL = 200525
        const val ERROR_INVALID_DESCRIPTION = 200526
        const val ERROR_INVALID_COMMENT = 200527
        const val ERROR_INVALID_CODE = 200528
        const val ERROR_GUID_DUPLICATE = 200529
    }
}