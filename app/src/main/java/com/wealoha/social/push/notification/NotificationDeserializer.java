package com.wealoha.social.push.notification;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Push提醒的反序列化
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-11 下午11:17:29
 */
public class NotificationDeserializer implements JsonDeserializer<Notification> {

	private static Map<String, Class<? extends Notification>> notificationMap = new HashMap<String, Class<? extends Notification>>();

	public void registerContentType(String type, Class<? extends Notification> typeClass) {
		notificationMap.put(type, typeClass);
	}

	@Override
	public Notification deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonObject()) {
			String type = json.getAsJsonObject().get("type").getAsString();
			Class<? extends Notification> messageClass = notificationMap.get(type);
			if (messageClass != null) {
				return context.deserialize(json, messageClass);
			}
		}
		return null;
	}
}
