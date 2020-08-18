package com.wealoha.social.presenters;

import com.wealoha.social.inject.Injector;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月21日
 */
public abstract class AbsPresenter {

	public AbsPresenter() {
		Injector.inject(this);
	}
}
