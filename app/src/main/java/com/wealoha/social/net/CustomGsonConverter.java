package com.wealoha.social.net;

import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.imagemap.HasImageMap;
import com.wealoha.social.event.TicketInvalidEvent;
import com.wealoha.social.utils.ImageCache;
import com.wealoha.social.utils.XL;

/**
 * 定制的Gson转换，遇到票无效，会发事件 {@link TicketInvalidEvent}
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午1:25:47
 */
public class CustomGsonConverter extends GsonConverter {

	private final String TAG = getClass().getSimpleName();
	private final Bus bus;

	public CustomGsonConverter(Gson gson, Bus bus) {
		super(gson);
		this.bus = bus;
	}

	@Override
	public Object fromBody(TypedInput input, Type type) throws ConversionException {
		Object result = super.fromBody(input, type);
		if (result instanceof ApiResponse) {
			@SuppressWarnings("unchecked")
			ApiResponse<ResultData> r = (ApiResponse<ResultData>) result;
			XL.d(TAG, "Result: " + r.getStatus());

			if (!r.isOk()) {
				if (r.getStatus() == ApiResponse.STATUS_CODE_FORBIDEN) {
					// 票被踢了
					XL.w(TAG, "票无效，发送事件..");
					bus.post(new TicketInvalidEvent());
				}
			}

			// 将imageMap存到cache中
			if (r.getData() != null && r.getData() instanceof HasImageMap) {
				ImageCache.setToCache(((HasImageMap) r.getData()).getImageMap());
			}
		}
		return result;
	}
}
