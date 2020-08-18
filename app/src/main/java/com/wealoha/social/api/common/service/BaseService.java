package com.wealoha.social.api.common.service;

import java.util.List;

public interface BaseService<T> {

	public interface ServiceCallback {

		public void success();

		public void failer();
	}

	public interface ServiceResultCallback<T> {

		public void success(List<T> list);

		public void failer();

		public void nomore();

		public void beforeSuccess();

	}
	public interface ServiceObjResultCallback<T> {

		public void success(T obj);

		public void failer();

		public void nomore();

		public void beforeSuccess();

	}
	public interface ServiceListResultCallback<T> {

		public void success(List<T> t);

		public void failer();
	}

}
