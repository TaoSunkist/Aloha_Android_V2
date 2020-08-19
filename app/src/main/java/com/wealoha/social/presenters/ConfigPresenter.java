package com.wealoha.social.presenters;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.common.ConstantsData;
import com.wealoha.social.api.ConstantsService;
import com.wealoha.social.beans.instagram.InstagramResult;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.config.IConfigView;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.ToastUtil;

/**
 * @author:sunkist
 * @description:用户设置界面的入口
 * @Date:2015年7月30日
 */
public class ConfigPresenter extends AbsPresenter {
	private IConfigView mIConfigView;
	private Context mCtx;
	@Inject
	ConstantsService constantsService;
	// 缓存的状态
	private int mClearCacheStatus;
	@Inject
	UserService mUserService;

	public ConfigPresenter(Context ctx, IConfigView iConfigView) {
		mIConfigView = iConfigView;
		mCtx = ctx;
	}

	/**
	 * @author: sunkist
	 * @description:清理缓存
	 * @date:2015年7月30日
	 */
	public void clearCache() {
		AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

			public void run() {
				ContextConfig.getInstance().putIntWithFilename("clearCache", 1);
				FileTools.deleteFile(FileTools.ROOT_PATH);
				FileTools.deleteFile(GlobalConstants.AppConstact.FILE_LOCAL);
				ContextConfig.getInstance().putIntWithFilename("clearCache", 0);
				mIConfigView.clearCacheSuccess();
			}
		});
	}

	/**
	 * @author: sunkist
	 * @description:检查更新
	 * @date:2015年7月30日
	 */
	public void checkNewVersion() {
		constantsService.get(new Callback<Result<ConstantsData>>() {

			@Override
			public void success(Result<ConstantsData> result, Response arg1) {
				if (result != null && result.isOk()) {
					if (result.data.hasUpdateVersion) {
						mIConfigView.updateDialog(result.data.updateDetails);
					} else {
						ToastUtil.longToast(mCtx, R.string.no_new_version);
					}
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.longToast(mCtx, R.string.network_error);
			}
		});

	}

	/**
	 * @author: sunkist
	 * @return
	 * @description:计算缓存的大小
	 * @date:2015年7月30日
	 */
	public void clacCacheSize() {
		mClearCacheStatus = ContextConfig.getInstance().getIntWithFilename("clearCache");
		if (mClearCacheStatus == 0) {// mClearCacheStatus:0 清理完成了
			// 去计算本地文件夹中存在多少缓存文件.
			AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

				public void run() {
					try {
						final String cacheSize = FileTools.FormetFileSize(FileTools.byFileGetFileSize(FileTools.ROOT_PATH));
						mIConfigView.setUiShowCacheSize(cacheSize);
					} catch (Throwable e) {
					}
				}
			});
		}
	}

	/**
	 * @author: sunkist
	 * @description:刷新Instagram
	 * @date:2015年7月30日
	 */
	public void refreshInstagram() {
		try {
			mUserService.loginType(new Callback<Result<InstagramResult>>() {

				@Override
				public void success(Result<InstagramResult> result, Response arg1) {
					mIConfigView.refreshInstagramSuccess(result);

				}

				@Override
				public void failure(RetrofitError arg0) {
				}
			});
		} catch (NullPointerException e) {
		}

	}
}
