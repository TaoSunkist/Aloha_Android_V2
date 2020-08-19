/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wealoha.social.beans

import android.content.Context
import com.sina.weibo.sdk.auth.Oauth2AccessToken

/**
 * 该类定义了微博授权时所需要的参数。
 *
 * @author SINA
 * @since 2013-10-07
 */
object AccessTokenKeeper {
    private const val PREFERENCES_NAME = "com_weibo_sdk_android"
    private const val KEY_UID = "uid"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_EXPIRES_IN = "expires_in"

    /**
     * 保存 Token 对象到 SharedPreferences。
     *
     * @param context
     * 应用程序上下文环境
     * @param token
     * Token 对象
     */
    @kotlin.jvm.JvmStatic
    fun writeAccessToken(
        context: Context?,
        token: Oauth2AccessToken?
    ) {
        if (null == context || null == token) {
            return
        }
        val pref = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_APPEND
        )
        val editor = pref.edit()
        editor.putString(KEY_UID, token.uid)
        editor.putString(KEY_ACCESS_TOKEN, token.token)
        editor.putLong(KEY_EXPIRES_IN, token.expiresTime)
        editor.commit()
    }

    /**
     * 从 SharedPreferences 读取 Token 信息。
     *
     * @param context
     * 应用程序上下文环境
     *
     * @return 返回 Token 对象
     */
    @kotlin.jvm.JvmStatic
    fun readAccessToken(context: Context?): Oauth2AccessToken? {
        if (null == context) {
            return null
        }
        val token = Oauth2AccessToken()
        val pref = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_APPEND
        )
        token.uid = pref.getString(KEY_UID, "")
        token.token = pref.getString(KEY_ACCESS_TOKEN, "")
        token.expiresTime = pref.getLong(KEY_EXPIRES_IN, 0)
        return token
    }

    /**
     * 清空 SharedPreferences 中 Token信息。
     *
     * @param context
     * 应用程序上下文环境
     */
    @kotlin.jvm.JvmStatic
    fun clear(context: Context?) {
        if (null == context) {
            return
        }
        val pref = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_APPEND
        )
        val editor = pref.edit()
        editor.clear()
        editor.commit()
    }
}