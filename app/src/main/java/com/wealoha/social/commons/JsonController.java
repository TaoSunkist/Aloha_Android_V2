package com.wealoha.social.commons;

import java.lang.reflect.Type;
import java.util.HashMap;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.inject.Injector;

public class JsonController {

	@Inject
	Gson gson;

	public JsonController() {
		Injector.inject(this);
	}

	public static final String TAG = JsonController.class.getSimpleName();
	private static JsonController mJsonController = new JsonController();

	public static JsonController getInstance() {
		return mJsonController;
	}

	public <T extends ResultData> Result<T> parseJsonEx(String jsonData, Type typeOfT) {
		return gson.fromJson(jsonData, typeOfT);
	}

	public static <T extends ResultData> Result<T> parseJson(String jsonData, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, typeOfT);
	}
	public static <T> T parseSinaJson(String jsonData, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, typeOfT);
	}
	public <T extends ResultData> HashMap<String, Message> parseJsonMap(String jsonData, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, typeOfT);
	}
}
