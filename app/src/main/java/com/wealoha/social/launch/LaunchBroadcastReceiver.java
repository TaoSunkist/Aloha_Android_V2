package com.wealoha.social.launch;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import androidx.fragment.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.NaviIntroActivity;
import com.wealoha.social.activity.WelcomeAct;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.BaseListApiService.ApiListCallback;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.api.SingletonFeedListApiService;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SingletonFeedFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.XL;

/**
 * 接收App呼起服务发出的广播
 *
 * @author javamonk
 * @date 2015-1-5 下午11:00:22
 * @see
 * @since
 */
public class LaunchBroadcastReceiver extends BroadcastReceiver {

    @Inject
    ContextUtil contextUtil;
    @Inject
    ServerApi mProfileService;
    SingletonFeedListApiService feedService = new SingletonFeedListApiService();
    @Inject
    ServerApi feedService2;
    private Context mContext;

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Injector.inject(this);
        mContext = context;
        String url = intent.getStringExtra(LaunchService.EXTENDED_DATA_URL);
        // Toast.makeText(context, url, Toast.LENGTH_LONG).show();
        XL.d(TAG, "收到广播: " + url);
        String[] uriAndQueryString = StringUtil.split(url, "\\?"); // 居然是正则！
        if (uriAndQueryString != null && uriAndQueryString.length > 1) {
            String uri = uriAndQueryString[0];
            Map<String, String> paramsMap = parseParams(uriAndQueryString[1]);
            if ("/user".equals(uri)) {
                // 打开user profile
                String userId = paramsMap.get("userId");
                String shareByUserId = paramsMap.get("shareby");
                if (StringUtil.isNotEmpty(userId)) {
                    openUserProfile(context, userId, shareByUserId);
                }
            } else if ("/post".equals(uri)) {
                String postid = paramsMap.get("postId");
                String shareByUserId = paramsMap.get("shareby");
                if (StringUtil.isNotEmpty(postid)) {
                    openPost(postid, shareByUserId);
                }
            }
        }
    }

    private Map<String, String> parseParams(String param) {
        if (StringUtil.isEmpty(param)) {
            return Collections.emptyMap();
        }
        HashMap<String, String> result = new HashMap<String, String>();
        String[] pairs = StringUtil.split(param, "&");
        for (String pair : pairs) {
            String[] knv = StringUtil.split(pair, "=");
            if (knv.length == 1) {
                result.put(knv[0], null);
            } else if (knv.length == 2) {
                result.put(knv[0], knv[1]);
            }
        }
        return result;
    }

    private void openPost(String postid, String shareByUserId) {
        User currentUser = contextUtil.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(mContext, WelcomeAct.class);
            mContext.startActivity(intent);
            return;
        }
        String ticket = contextUtil.getCurrentTicket();
        if (currentUser != null && ticket != null) {
            goSingletonFeed(postid);
        } else {
            Intent i = new Intent(mContext, WelcomeAct.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
        try {
            if (!TextUtils.isEmpty(shareByUserId)) {
                reqPostWebCallApp(postid, shareByUserId);
            }
        } catch (Throwable e) {
        }
    }

    private void reqPostWebCallApp(String postid, String shareByUserId) {
//		feedService2.feedWebCallAlohaApp(postid, shareByUserId, new Callback<ResultData>() {
//
//			@Override
//			public void failure(RetrofitError arg0) {
//
//			}
//
//			@Override
//			public void success(ResultData arg0, Response arg1) {
//
//			}
//		});
    }

    /**
     * @param context
     * @param userId
     * @param shareByUserId
     * @Description:
     * @see:
     * @since:
     * @description 通过网页呼起ProfileTestFragment时，需要去发送一次该请求
     * @author: sunkist
     * @date:2015-3-9
     */
    private void openUserProfile(Context context, String userId, String shareByUserId) {
        // TODO 打开用户profile(注意处理好未登录情况)
        User currentUser = contextUtil.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(context, NaviIntroActivity.class);
            context.startActivity(intent);
            return;
        }
        String ticket = contextUtil.getCurrentTicket();
        if (StringUtils.isNotBlank(shareByUserId)) {
            mProfileService.recordCardClick(userId, shareByUserId, new retrofit.Callback<ApiResponse<ResultData>>() {

                @Override
                public void failure(RetrofitError arg0) {
                }

                @Override
                public void success(ApiResponse<ResultData> arg0, Response arg1) {
                }
            });
        }
        if (currentUser != null && ticket != null) {
            getUserProfile(userId);
        } else {
            Intent i = new Intent(context, NaviIntroActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private void startProfileFragment(Class<? extends Fragment> fragmentClass, User user) {

        if (mContext == null) {
            return;
        }
        Bundle b = new Bundle();
        b.putString("refer_key", GlobalConstants.WhereIsComeFrom.URL_SCHEME_TO_PROFILE);
        b.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, fragmentClass);
        if (user != null) {
            b.putParcelable(User.TAG, user);
        }
        //push 进入要开启手势锁
        ContextConfig.getInstance().putBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY, true);
        b.putBoolean(FragmentWrapperActivity.IS_FROM_WEB_PAGE_KEY, true);

        // 使用Activity打开Fragment: " + fragmentClass.getName());
        Intent intent = new Intent(mContext, FragmentWrapperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }

    private void startFragment(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        if (mContext == null || bundle == null) {
            return;
        }
        //push 进入要开启手势锁
        ContextConfig.getInstance().putBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY, true);
        bundle.putBoolean(FragmentWrapperActivity.IS_FROM_WEB_PAGE_KEY, true);

        bundle.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, SingletonFeedFragment.class);
        Intent intent = new Intent(mContext, FragmentWrapperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private void goSingletonFeed(String postid) {
        feedService.getList(null, 1, Direct.Early, postid, new ApiListCallback<Post>() {

            @Override
            public void success(@NotNull List<? extends Post> list, @NotNull String nextCursorId) {
                Bundle bundle = new Bundle();

                if (list != null && list.get(0) != null) {
                    bundle.putSerializable(Post.TAG, (Post) list.get(0));
                }
                startFragment(SingletonFeedFragment.class, bundle);
            }

            @Override
            public void fail(ApiErrorCode code, Exception exception) {

            }
        });
    }

    private void getUserProfile(String userid) {
        if (mProfileService == null) {
            return;
        }
        mProfileService.view(userid, new Callback<ApiResponse<ProfileData>>() {

            @Override
            public void success(ApiResponse<ProfileData> apiResponse, Response arg1) {
                if (apiResponse != null && apiResponse.isOk()) {
                    startProfileFragment(Profile2Fragment.class, apiResponse.getData().user);
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
            }
        });
    }
}
