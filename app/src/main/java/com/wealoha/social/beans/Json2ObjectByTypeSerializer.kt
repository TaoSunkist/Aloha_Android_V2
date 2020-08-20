package com.wealoha.social.beans

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * 使用api返回json的type属性做继承类型对象的序列化和反序列化
 *
 * 参考 { AlohaModule#provideGson()}
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:35:38
 */
class Json2ObjectByTypeSerializer : JsonDeserializer<Any?>,
    JsonSerializer<Any?> {
    private val gson = Gson()
    fun registerContentType(
        type: String,
        typeClass: Class<out Any>
    ) {
        typesMap[type] = typeClass
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Any? {
        if (json.isJsonObject) {
            val typeElement = json.asJsonObject["type"]
            if (typeElement != null) {
                val type = typeElement.asString
                val clazz =
                    typesMap[type]
                if (clazz != null) {
                    return context.deserialize<Any>(json, clazz)
                }
            }
        }
        return null
    }

    override fun serialize(
        `object`: Any?,
        typeOfT: Type,
        context: JsonSerializationContext
    ): JsonElement {
        // 序列化对象的时候，子类的字段不会正确序列化，这里要特殊处理
        return gson.toJsonTree(`object`)
    }

    companion object {
        private val typesMap: MutableMap<String, Class<out Any>> =
            HashMap()
    }
}