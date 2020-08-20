package com.wealoha.social.api;

import java.util.List;

import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.widget.BaseListApiAdapter;

/**
 * 所有和api交互的list类型
 * 
 * @author javamonk
 * @createTime 2015年3月4日 下午11:02:05
 */
public interface BaseListApiService<E, P> {

	/**
	 * Api列表数据加载异步回调
	 * 
	 * @author javamonk
	 * @createTime 2015年3月6日 上午11:28:42
	 */
	public interface ApiListCallback<E> {

		/**
		 * 加载数据成功
		 * 
		 * @param list
		 * @param nextCursorId
		 */
		public void success(List<E> list, String nextCursorId);

		/**
		 * 加载数据出错
		 * 
		 * @param code
		 *            api的错误码
		 * @param exception
		 *            异常，可能为null
		 */
		public void fail(ApiErrorCode code, Exception exception);
	}

	/***
	 * @author dell-pc
	 *
	 * @param 只返回一个数据
	 */
	public interface ApiCallback<E> {

		/**
		 * 加载数据成功
		 * 
		 * @param list
		 * @param nextCursorId
		 */
		public void success(E data);

		/**
		 * 加载数据出错
		 * 
		 * @param code
		 *            api的错误码
		 * @param exception
		 *            异常，可能为null
		 */
		public void fail(ApiErrorCode code, Exception exception);
	}

	public interface ListContextCallback<E> {

		/**
		 * 加载数据成功
		 * 
		 * @param list
		 * @param preCursorId
		 * @param nextCursorId
		 */
		public void success(List<E> list, String preCursorId, String nextCursorId);

		/**
		 * 加载数据出错
		 * 
		 * @param code
		 *            api的错误码
		 * @param exception
		 *            异常，可能为null
		 */
		public void fail(ApiErrorCode code, Exception exception);
	}

	public interface NoResultCallback {

		public void success();

		public void fail(ApiErrorCode code, Exception exception);
	}

	public interface AdapterListDataCallback<E> {

		/***
		 * service 通过这个callback 获取adapter中的list数据
		 * 
		 * @return
		 * @return List<E>
		 */
		public List<E> getListData();
	}

	/**
	 * 是否需要把api取到的数据反转(不用自己反转 {@link BaseListApiAdapter} 会反转)
	 * 
	 * @return
	 */
	public boolean needReverse();

	/**
	 * 是否需要把数据放到开头而不是结尾
	 * 
	 * @return
	 */
	public boolean appendToHeader();

	/**
	 * 是否支持向前取数据
	 * 
	 * @return
	 */
	public boolean supportPrev();

	/**
	 * 是否支持根据数据定位
	 * 
	 * @return
	 */
	public boolean supportContextByCursor();

	/**
	 * 获取列表数据
	 * 
	 * @param cursor
	 * @param count
	 * @param direct
	 *            加载数据的方向
	 * @param param
	 *            参数，每个service自行定义
	 * @param callback
	 * @see 参见 Notify2Service#getList(String, int, Void, ApiListCallback) 的错误处理
	 */
	public void getList(String cursor, int count, Direct direct, P param, final ApiListCallback<E> callback);

	/**
	 * 获取列表数据，以cursor为参照，上下各取一部分
	 * 
	 * @param cursor
	 * @param count
	 * @param param
	 * @param callback
	 */
	public void getListWithContext(String cursor, int count, P param, final ListContextCallback<E> callback);

	/***
	 * 设置能够获取adapter 中的listdata 的callback
	 * 
	 * @param callback
	 * @return void
	 */
	public void setAdapterListCallback(AdapterListDataCallback<E> callback);

	/***
	 * 
	 * 加载list 中的图片到缓存中
	 * 
	 * @return void
	 */
	public void fetchPhoto(int firstVisibleItem, int totalItemCount, boolean direction, int mScreenWidth);

}
