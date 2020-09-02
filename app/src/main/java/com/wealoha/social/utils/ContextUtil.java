package com.wealoha.social.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.fragment.app.Fragment;

import com.lidroid.xutils.http.RequestParams;
import com.wealoha.social.AppApplication;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.CacheManager;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.fragment.ChatFragment;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.store.UserAgentProvider;

import dagger.Lazy;

/**
 * 获取一些上下文的数据<br/>
 *
 * <span style="color:red">注意！！ 如果使用了注入的属性，需要先调用 init()</span>
 *
 * @author javamonk
 * @date 2014-10-30 上午11:57:56
 * @see
 * @since
 */
public class ContextUtil {

    private boolean foreground;

    private Activity mAct;
    private Fragment mFrag;

    private ChatFragment chatFrag;

    private MainAct mainAct;

    private String TAG = getClass().getSimpleName();

    @Inject
    Lazy<GuidUtil> guidUtil;

    @Inject
    Lazy<PushUtil> pushUtil;

    @Inject
    Lazy<UserAgentProvider> userAgentProvider;

    @Inject
    Lazy<PackageInfo> packageInfo;

    @Inject
    Lazy<CacheManager> cacheManager;

    @Inject
    Lazy<Context> context;

    // 用Lazy避免循环依赖

    public ContextUtil() {
        Injector.inject(this);
    }

    /**
     * 只需要调用一次！！
     */
    public void init() {
        if (guidUtil == null) {
            synchronized (ContextUtil.class) {
                if (guidUtil == null) {
                    // lazy，避免循环初始化stackoverflow
                    // Injector.inject(this);
                }
            }
        }
    }

    /**
     * 添加通用请求头(包含票)
     *
     * @param requestParams
     */
    public void addGeneralHttpHeaders(RequestParams requestParams) {
        addGeneralHttpHeaders(requestParams, true);
    }

    /**
     * 添加通用请求头
     *
     * @param requestParams
     * @param includeTicket 是否包含票
     */
    public void addGeneralHttpHeaders(RequestParams requestParams, boolean includeTicket) {
        init();

        // 注意！！！这里修改完修改要改 AlohaModule.provideRequestInterceptor() 保持一致

        // 版本号，Android设备信息(scale 1.5等) 例如 (Android ......MIUI)
        requestParams.setHeader("User-Agent", userAgentProvider.get().getUserAgent());

        // requestParams.addHeader("Accept-Encoding", "gzip");
        // 唯一GUID
        requestParams.setHeader("User-Device-Id", guidUtil.get().getGuid());
        // 设备信息
        requestParams.setHeader("User-Device", userAgentProvider.get().getDevice());
        // requestParams.setHeader("Accept",
        // GlobalConstants.Http.CONTENT_TYPE_JSON);
        if (includeTicket) {
            String t = getCurrentTicket();
            if (t != null) {
                requestParams.setHeader("Cookie", "t=" + t);
            }
        }
    }

    /**
     * 当前应用是否打开
     *
     * @return
     */
    public boolean isForeground() {
        return foreground;
    }

    /**
     * 设置当前应用是否打开
     *
     * @param foreground
     */
    public void setForeground(boolean foreground) {
        XL.d(TAG, "设置app Foreground: " + foreground);
        this.foreground = foreground;
    }

    /**
     * 当前应用act
     *
     * @return
     */
    public void setForegroundAct(Activity act) {
        this.mAct = act;
    }

    public Activity getForegroundAct() {
        return this.mAct;
    }

    /**
     * 删除当前act
     *
     * @return
     */
    public void deleteForegroundAct() {
        this.mAct = null;
        this.mFrag = null;
    }

    /**
     * 当前应用Fragment
     *
     * @return
     */
    public void setForegroundFrag(Fragment frag) {
        this.mFrag = frag;
    }

    public Fragment getForegroundFrag() {
        return this.mFrag;
    }

    /**
     * 删除当前Frag记录
     *
     * @return
     */
    public void deleteForegroundFrag() {
        this.mFrag = null;
    }

    public void setChatFragment(ChatFragment cf) {
        this.chatFrag = cf;
    }

    public ChatFragment getChatFragment() {
        return this.chatFrag;
    }

    /**
     * AppApplication上保存的值不易丢失，这个ContextUtil每次开app都会new出来，保存啥都不行
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public ContextHolder getContextHolder() {
        init();

        if (AppApplication.contextHolder == null) {
            ContextHolder contextHolder = cacheManager.get().globalRestore(CacheKey.ContextHolder);
            if (contextHolder == null) {
                XL.d(TAG, "新建ContextHolder");
                contextHolder = new ContextHolder();
            } else {
                XL.d(TAG, "使用Cache里的ContextHolder");
            }
            AppApplication.contextHolder = contextHolder;
            AppApplication.user = contextHolder.currentUser;
        }
        return AppApplication.contextHolder;
    }

    public void saveContextHolder() {
        XL.d(TAG, "保存ContextHolder");
        cacheManager.get().globalSave(CacheKey.ContextHolder, getContextHolder());
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public User getCurrentUser() {
        return getContextHolder().currentUser;
    }

    /**
     * 修改当前用户(当获取用户profile或者获得了用户最新profile之后应当调用)
     *
     * @param user
     */
    public void setCurrentUser(User user) {
        init();
        ContextHolder contextHolder = getContextHolder();
        // 此处有NP 尝试修补
        User currentUser = contextHolder.currentUser;
        if (user != null && currentUser != null) {
            user.setShowAlohaTimeDialog(currentUser.isShowAlohaTimeDialog());
            user.setShowFeedDialog(currentUser.isShowFeedDialog());
        }
        contextHolder.currentUser = user;
        saveContextHolder();
        AppApplication.user = user;
    }

    /**
     * 获取当前的票
     *
     * @return
     */
    public String getCurrentTicket() {
        init();

        return getContextHolder().currentUserTicket;
    }

    public void setCurrentTicket(String ticket) {
        init();

        getContextHolder().currentUserTicket = ticket;
        saveContextHolder();
    }

    public void setCurrentSinaWbToken(String token) {
        init();

        getContextHolder().sinaWbToken = token;
        saveContextHolder();
    }

    public void setCurrentFacebookWbToken(String token) {
        init();

        getContextHolder().facebookWbToken = token;
        saveContextHolder();
    }

    public String getCurrentFacebookWbToken() {
        init();
        return getContextHolder().facebookWbToken;
    }

    public String getCurrentSinaWbToken() {
        init();
        return getContextHolder().sinaWbToken;
    }

    /**
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: isInstagram
     * @Description: 设置是否instagram同步
     */
    public boolean isInstagram() {
        init();
        return getContextHolder().isInstagram;
    }

    public void setInstagram(boolean isInstagram) {
        getContextHolder().isInstagram = isInstagram;
        saveContextHolder();
    }

    /**
     * 设置用户手机号登录
     * <p>
     * isAccountPhoneNumber
     */
    public void setPassportPhoneNumber(boolean isPassportPhoneNumber) {
        getContextHolder().isPassportPhoneNumber = isPassportPhoneNumber;
        saveContextHolder();
    }

    /**
     * 用户是否手机号登录
     *
     * @return
     */
    public boolean isPassportPhoneNumber() {
        return getContextHolder().isPassportPhoneNumber;
    }

    /**
     * 设置用户微博登录
     *
     * @ isAccountSinaWeibo
     */

    public void setPassportSinaWeibo(boolean isPassportSinaWeibo) {
        getContextHolder().isPassportSinaWeibo = isPassportSinaWeibo;

        saveContextHolder();
    }

    public void setPassportFacebook(boolean isPassportSinaWeibo) {
        getContextHolder().isPassportFacebookWeibo = isPassportSinaWeibo;

        saveContextHolder();
    }

    public boolean isPassportFacebook() {
        return getContextHolder().isPassportFacebookWeibo;
    }

    /**
     * 用户是否微博登录
     *
     * @return
     */
    public boolean isPassportSinaWeibo() {
        return getContextHolder().isPassportSinaWeibo;
    }

    /**
     * 取当前的用户手机号
     *
     * @return
     */
    public String getAccountPhoneNumber() {
        return getContextHolder().accountPhoneNumber;
    }

    public void setAccountPhoneNumber(String accountPhoneNumber) {
        getContextHolder().accountPhoneNumber = accountPhoneNumber;
        saveContextHolder();
    }

    /**
     * 获取开机图片
     *
     * @return
     */
    public String getStartupImageId() {
        return getContextHolder().startupImageId;
    }

    public void setStartupImageId(String id) {
        getContextHolder().startupImageId = id;
        saveContextHolder();
    }

    /**
     * 获取开机图片的本地路径
     *
     * @return
     */
    public String getStartupImagePath() {
        return getContextHolder().startupImagePath;
    }

    public void setStartupImagePath(String Path) {
        getContextHolder().startupImagePath = Path;
        saveContextHolder();
    }

    /**
     * 获取显示广告的间隔
     *
     * @return
     */
    public int getStartImageIntervalMinutes() {
        return getContextHolder().startupImageShowIntervalMinutes;
    }

    public void setStartImageIntervalMinutes(int minutes) {
        getContextHolder().startupImageShowIntervalMinutes = minutes;
        saveContextHolder();
    }

    /**
     * 获取用户筛选的地区
     *
     * @return
     */
    public String getFilterRegion() {
        return getContextHolder().filterRegion;
    }

    public void setFilterRegion(String region) {
        getContextHolder().filterRegion = region;
        saveContextHolder();
    }

    /**
     * 用户是否解锁高级功能
     *
     * @return
     */
    public boolean getProfeatureEnable() {
        return getContextHolder().profeatureEnable;
    }

    public void setProfeatureEnable(boolean lock) {
        getContextHolder().profeatureEnable = lock;
        saveContextHolder();
    }

    /**
     * check new version
     */
    public boolean hasNewVersion() {
        return getContextHolder().haveNewVersion;
    }

    public void setHasNewestVersion(boolean hasNewVersion) {
        if (hasNewVersion == hasNewVersion()) {
            return;
        }
        getContextHolder().haveNewVersion = hasNewVersion;
        saveContextHolder();
    }

    public String getNewVersionDetails() {
        return getContextHolder().newVersionDetails;
    }

    public void setNewVersionDetails(String value) {
        if (StringUtil.equals(value, getNewVersionDetails())) {
            return;
        }
        getContextHolder().newVersionDetails = value;
        saveContextHolder();
    }

    /**
     * 清理全部缓存(除了Ticket和User)
     */
    public void cleanAllCaches() {
        // 第一页feed
        init();
        cacheManager.get().save(FeedFragment.class, CacheKey.FirstPageFeed, null);
        cacheManager.get().save(ChatFragment.class, CacheKey.FirstPageInboxSession, null);
    }

    /**
     * 清理登录状态
     */
    public void cleanLoginStatus() {
        setCurrentTicket(null);
        setCurrentUser(null);
        setPassportPhoneNumber(false);
        setPassportSinaWeibo(false);
        setAccountPhoneNumber(null);
        pushUtil.get().setTokenUnbind();

        AppApplication.contextHolder = new ContextHolder();
        saveContextHolder();

        cleanAllCaches();
    }

    /**
     * 获取主导航界面
     *
     * @return
     */
    public MainAct getMainAct() {
        return mainAct;
    }

    public void setMainAct(MainAct mainAct) {
        this.mainAct = mainAct;
    }

    /**
     * 拼目的字符串！！
     *
     * @param purposeKeys
     * @return
     */
    public String formatPurposes(List<String> purposeKeys) {
        if (purposeKeys == null || purposeKeys.size() == 0) {
            return null;
        }

        List<String> purposes = new ArrayList<String>();
        for (String p : purposeKeys) {
            for (String pp : StringUtil.split(p, "-")) { // 兼容下，ios在用 xxx-xxx-xx
                String purpose = getUserPurpose(pp);
                if (purpose != null) {
                    purposes.add(purpose);
                }
            }
        }
        if (purposes.size() < 3) {
            return StringUtil.join(" & ", purposes);
        }

        String last = purposes.remove(purposes.size() - 1);
        return StringUtil.join(", ", purposes) + " & " + last;
    }

    /**
     * 转换用户的目的
     *
     * @return
     * @ code
     */
    public String getUserPurpose(String purpose) {
        if (StringUtil.isEmpty(purpose)) {
            return null;
        }
        init();

        if ("Relationship".equals(purpose)) {
            return context.get().getString(R.string.lovers);
        }
        if ("Friends".equals(purpose)) {
            return context.get().getString(R.string.friend);
        }
        if ("Other".equals(purpose)) {
            return context.get().getString(R.string.others);
        }
        return null;
    }

    /**
     * 转换用户的类型
     *
     * @param tag
     * @return
     */
    public String getUserTag(String tag) {
        if (StringUtil.isEmpty(tag)) {
            return null;
        }

        if ("Bear".equals(tag)) {
            return context.get().getString(R.string.bear);
        } else if ("Muscle".equals(tag)) {
            return context.get().getString(R.string.muscle);
        } else if ("Average".equals(tag)) {
            return context.get().getString(R.string.average);
        } else if ("Slim".equals(tag)) {
            return context.get().getString(R.string.slim);
        }
        return null;
    }

    /**
     * On Android, callbacks will be executed on the main thread.
     *
     * @author javamonk
     * @date 2014-11-20 下午1:14:35
     * @see
     * @since
     */
    private class RetrofitCallback implements retrofit.Callback<ApiResponse<ResultData>> {

        private final boolean showToaster;

        public RetrofitCallback(boolean showToaster) {
            super();
            this.showToaster = showToaster;
        }

        @Override
        public void failure(RetrofitError error) {
            if (showToaster) {
                // 请求失败
                ToastUtil.shortToast(context.get(), context.get().getString(R.string.data_request_failed));
            }
            XL.w(TAG, "API请求失败", error);
        }

        public void success(com.wealoha.social.beans.ApiResponse<ResultData> apiResponse, retrofit.client.Response response) {
            XL.d(TAG, "API请求成功: " + apiResponse);
            if (apiResponse.isOk()) {
                return;
            }
            if (apiResponse.getStatus() == ApiResponse.STATUS_CODE_FORBIDEN) {
                // 票过期了
                XL.d(TAG, "票过期啦!!");
                // setCurrentTicket(null);
                // setCurrentUser(null);
                // TODO 调用退出登录
            }
        }

        ;
    }

    /**
     * API请求使用callback，处理票过期和通用错误
     *
     * @param showToaster 如果出错了，是否显示对话框
     * @return
     */
    public retrofit.Callback<ApiResponse<ResultData>> getCommonResultCallback(boolean showToaster) {
        init();

        return new RetrofitCallback(showToaster);
    }

    /**
     * 使用的api是否是测试环境的
     *
     * @return
     */
    public boolean isTestingApi() {
        try {
            InputStream is = context.get().getAssets().open("test");
            is.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            XL.w(TAG, "测试api失败", e);
        }
        return false;
    }
}
