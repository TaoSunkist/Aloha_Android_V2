package com.wealoha.social.beans;

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
import com.wealoha.social.inject.AlohaModule;

/**
 * 使用api返回json的type属性做继承类型对象的序列化和反序列化
 * 
 * 参考 { AlohaModule#provideGson()}
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午11:35:38
 */
public class Json2ObjectByTypeSerializer implements JsonDeserializer<Object>, JsonSerializer<Object> {

	private final Gson gson = new Gson();

	private static Map<String, Class<? extends Object>> typesMap = new HashMap<String, Class<? extends Object>>();

	public void registerContentType(String type, Class<? extends Object> typeClass) {
		typesMap.put(type, typeClass);
	}

	@Override
	public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonObject()) {
			JsonElement typeElement = json.getAsJsonObject().get("type");
			if (typeElement != null) {
				String type = typeElement.getAsString();
				Class<? extends Object> clazz = typesMap.get(type);
				if (clazz != null) {
					return context.deserialize(json, clazz);
				}
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(Object object, Type typeOfT, JsonSerializationContext context) {
		// 序列化对象的时候，子类的字段不会正确序列化，这里要特殊处理
		return gson.toJsonTree(object);
	}
}
