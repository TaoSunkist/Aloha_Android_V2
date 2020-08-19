package com.wealoha.social.beans.message

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * Created by walker on 14-4-27.
 */
class MessageSerializer : JsonDeserializer<Message?>,
    JsonSerializer<Message?> {
    private val gson = Gson()
    fun registerContentType(
        type: String,
        typeClass: Class<out Message>
    ) {
        messageMap[type] = typeClass
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Message? {
        if (json.isJsonObject) {
            val type = json.asJsonObject["type"].asString
            val messageClass =
                messageMap[type]
            if (messageClass != null) {
                return context.deserialize<Message>(
                    json,
                    messageClass
                )
            }
        }
        return null
    }

    override fun serialize(
        message: Message?,
        typeOfT: Type,
        context: JsonSerializationContext
    ): JsonElement {
        // 序列化Message的时候，子类的字段不会正确序列化，这里要特殊处理
        return gson.toJsonTree(message)
    }

    companion object {
        private val messageMap: MutableMap<String, Class<out Message>> =
            HashMap()
    }
}