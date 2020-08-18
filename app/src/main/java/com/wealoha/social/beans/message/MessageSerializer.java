package com.wealoha.social.beans.message;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Created by walker on 14-4-27.
 */
public class MessageSerializer implements JsonDeserializer<Message>, JsonSerializer<Message> {

	private final Gson gson = new Gson();

	private static Map<String, Class<? extends Message>> messageMap = new HashMap<String, Class<? extends Message>>();

	public void registerContentType(String type, Class<? extends Message> typeClass) {
		messageMap.put(type, typeClass);
	}

	@Override
	public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonObject()) {
			String type = json.getAsJsonObject().get("type").getAsString();
			Class<? extends Message> messageClass = messageMap.get(type);
			if (messageClass != null) {
				return context.deserialize(json, messageClass);
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(Message message, Type typeOfT, JsonSerializationContext context) {
		// 序列化Message的时候，子类的字段不会正确序列化，这里要特殊处理
		return gson.toJsonTree(message);
	}
}
